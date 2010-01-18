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
 * EagerFunctionInfo.java
 * Creation date: April 27, 2006
 * By: Raymond Cypher
 */
package org.openquark.cal.compiler;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openquark.cal.internal.module.Cal.Collections.CAL_ArrayPrimitives_internal;
import org.openquark.cal.internal.module.Cal.Core.CAL_Bits_internal;
import org.openquark.cal.internal.module.Cal.Core.CAL_Prelude_internal;
import org.openquark.cal.internal.module.Cal.Core.CAL_Record_internal;
import org.openquark.cal.module.Cal.Collections.CAL_Array;
import org.openquark.cal.module.Cal.Core.CAL_Char;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.module.Cal.Utilities.CAL_Math;


/**
 * Contains info on the functions which can be eagerly evaluated in a CAL runtime.
 * An eagerly evaluated function is one which will be immediately evaluated in a
 * lazy context if it can be determined that all the function arguments are in WHNF.  
 * <p>
 * Currently this contains:
 * Map of ModuleName -> (String Set).  This maps module names to a map of Set of String which contains
 * function names. These functions will be eagerly evaluated. Note that various integral division
 * functions are also included here which are not always eagerly evaluated- the code generator must
 * check that at compilation time the divisor is not 0.
 * 
 * @author Raymond Cypher
 */
final class EagerFunctionInfo {
    
    private EagerFunctionInfo() {}

    /**    
     * @param name
     * @return true if the named function is marked as being eagerly evaluated.
     */
    final static boolean canFunctionBeEagerlyEvaluated(QualifiedName name) {
        Set<String> s = eagerlyEvaluatedFunctions.get(name.getModuleName());
        if (s != null) {
            return s.contains(name.getUnqualifiedName());
        }
        
        return false;
    }
    
    
    /**
     * Map of ModuleName -> (String Set).  This maps module names to a map of Set of String which contains
     * function names. These functions will be eagerly evaluated. Note that various integral division
     * functions are also included here which are not always eagerly evaluated- the code generator must
     * check that at compilation time the divisor is not 0.
     * 
     * Since this map is only mutated by code in the static initialization block
     * it isn't synchronized.  If, however, the map is ever mutated by code outside
     * the static initialization block it will need to be synchronized.
     */
    static private final Map<ModuleName, Set<String>> eagerlyEvaluatedFunctions = new HashMap<ModuleName, Set<String>>();

    static {
        // These are sets of functions, by Module, which can be treated specially.
        // When encountered in a lazy context these functions will be directly evaluated,
        // rather than building a lazy graph, if all the arguments are known to be in WHNF.
        // This is an optimization intended for functions where, when the arguments are already
        // evaluated, it is cheaper to simply evaluate the function than it is to build a
        // graph of the function application.
        // NOTE: functions optimized in this fashion must be safe.  i.e. there are no possible 
        // side effects, no possibility of a thrown exception, etc.
        // This is because the optimization can cause a change in reduction order, which is only 
        // safe if the function has no side effects and will never fail (i.e. throw an exception).
        
        {
            final Set<String> preludeSet = new HashSet<String> ();
            eagerlyEvaluatedFunctions.put (CAL_Prelude_internal.MODULE_NAME, Collections.unmodifiableSet(preludeSet));
            
            // Add the primitive ops that make sense.
            preludeSet.add(CAL_Prelude_internal.Functions.equalsInt.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.notEqualsInt.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.greaterThanInt.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.greaterThanEqualsInt.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.lessThanInt.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.lessThanEqualsInt.getUnqualifiedName());
            
            preludeSet.add(CAL_Prelude_internal.Functions.addInt.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.subtractInt.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.multiplyInt.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.negateInt.getUnqualifiedName());     
            
            preludeSet.add(CAL_Prelude_internal.Functions.equalsDouble.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.notEqualsDouble.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.greaterThanDouble.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.greaterThanEqualsDouble.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.lessThanDouble.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.lessThanEqualsDouble.getUnqualifiedName());
            
            preludeSet.add(CAL_Prelude_internal.Functions.addDouble.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.subtractDouble.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.multiplyDouble.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.negateDouble.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.remainderDouble.getUnqualifiedName()); //remainderDouble never throws an exception.
            
            preludeSet.add(CAL_Prelude.Functions.truncate.getUnqualifiedName());       
            
            preludeSet.add(CAL_Prelude_internal.Functions.equalsChar.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.notEqualsChar.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.greaterThanChar.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.greaterThanEqualsChar.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.lessThanChar.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.lessThanEqualsChar.getUnqualifiedName());        
            
            preludeSet.add(CAL_Prelude_internal.Functions.equalsLong.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.notEqualsLong.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.greaterThanLong.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.greaterThanEqualsLong.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.lessThanLong.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.lessThanEqualsLong.getUnqualifiedName());
            
            preludeSet.add(CAL_Prelude_internal.Functions.addLong.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.subtractLong.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.multiplyLong.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.negateLong.getUnqualifiedName());       
            preludeSet.add(CAL_Prelude_internal.Functions.longToDouble.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.divideDouble.getUnqualifiedName());  
            
            preludeSet.add(CAL_Prelude_internal.Functions.equalsShort.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.notEqualsShort.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.greaterThanShort.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.greaterThanEqualsShort.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.lessThanShort.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.lessThanEqualsShort.getUnqualifiedName());
            
            preludeSet.add(CAL_Prelude_internal.Functions.addShort.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.subtractShort.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.multiplyShort.getUnqualifiedName());       
            preludeSet.add(CAL_Prelude_internal.Functions.negateShort.getUnqualifiedName()); 
    
            preludeSet.add(CAL_Prelude_internal.Functions.equalsByte.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.notEqualsByte.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.greaterThanByte.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.greaterThanEqualsByte.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.lessThanByte.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.lessThanEqualsByte.getUnqualifiedName());
    
            preludeSet.add(CAL_Prelude_internal.Functions.addByte.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.subtractByte.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.multiplyByte.getUnqualifiedName());       
            preludeSet.add(CAL_Prelude_internal.Functions.negateByte.getUnqualifiedName());          
    
            preludeSet.add(CAL_Prelude_internal.Functions.equalsFloat.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.notEqualsFloat.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.greaterThanFloat.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.greaterThanEqualsFloat.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.lessThanFloat.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.lessThanEqualsFloat.getUnqualifiedName());
    
            preludeSet.add(CAL_Prelude_internal.Functions.addFloat.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.subtractFloat.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.multiplyFloat.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.divideFloat.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.negateFloat.getUnqualifiedName()); 
            preludeSet.add(CAL_Prelude_internal.Functions.remainderFloat.getUnqualifiedName());
            
            //the primitive casting functions
            //note casting functions for non-primitives (such as Integer and Decimal cannot be included
            
            preludeSet.add(CAL_Prelude_internal.Functions.byteToByte.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.byteToShort.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.byteToChar.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.byteToInt.getUnqualifiedName());       
            preludeSet.add(CAL_Prelude_internal.Functions.byteToLong.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.byteToFloat.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.byteToDouble.getUnqualifiedName());
            
            preludeSet.add(CAL_Prelude_internal.Functions.shortToByte.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.shortToShort.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.shortToChar.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.shortToInt.getUnqualifiedName());       
            preludeSet.add(CAL_Prelude_internal.Functions.shortToLong.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.shortToFloat.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.shortToDouble.getUnqualifiedName());     
            
            preludeSet.add(CAL_Prelude_internal.Functions.charToByte.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.charToChar.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.charToShort.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.charToInt.getUnqualifiedName());       
            preludeSet.add(CAL_Prelude_internal.Functions.charToLong.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.charToFloat.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.charToDouble.getUnqualifiedName()); 
            
            preludeSet.add(CAL_Prelude_internal.Functions.intToByte.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.intToShort.getUnqualifiedName());       
            preludeSet.add(CAL_Prelude_internal.Functions.intToChar.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.intToInt.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.intToLong.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.intToFloat.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.intToDouble.getUnqualifiedName());
            
            preludeSet.add(CAL_Prelude_internal.Functions.longToByte.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.longToShort.getUnqualifiedName());       
            preludeSet.add(CAL_Prelude_internal.Functions.longToChar.getUnqualifiedName());        
            preludeSet.add(CAL_Prelude_internal.Functions.longToInt.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.longToLong.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.longToFloat.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.longToDouble.getUnqualifiedName());  
            
            preludeSet.add(CAL_Prelude_internal.Functions.floatToByte.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.floatToShort.getUnqualifiedName());       
            preludeSet.add(CAL_Prelude_internal.Functions.floatToChar.getUnqualifiedName());        
            preludeSet.add(CAL_Prelude_internal.Functions.floatToInt.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.floatToLong.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.floatToFloat.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.floatToDouble.getUnqualifiedName());
            
            preludeSet.add(CAL_Prelude_internal.Functions.doubleToByte.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.doubleToShort.getUnqualifiedName());       
            preludeSet.add(CAL_Prelude_internal.Functions.doubleToChar.getUnqualifiedName());        
            preludeSet.add(CAL_Prelude_internal.Functions.doubleToInt.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.doubleToLong.getUnqualifiedName());       
            preludeSet.add(CAL_Prelude_internal.Functions.doubleToFloat.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.doubleToDouble.getUnqualifiedName());
            
            // These String functions cannot be optimized since they are
            // foreign functions which act on a java.lang.String object.  If the
            // object is null they can throw an exception.  A null String
            // cannot be directly created in CAL source, but it can be marshaled
            // from the result of a foreign function call.
            //preludeSet.add("equalsString");
            //preludeSet.add("notEqualsString");
            //preludeSet.add("greaterThanString");
            //preludeSet.add("greaterThanEqualsString");
            //preludeSet.add("lessThanString");
            //preludeSet.add("lessThanEqualsString");
            
            // This function is now in the String module:
            //preludeSet.add("appendCharToString"); 
            
            // These can only be optimized if the second argument is not zero.
            // They are handled specially in the code generation and the 
            // optimization is only applied if the second argument can be
            // determined to be non-zero at compile time.
            // Adding or removing a function of this nature requires updating
            // the code generation.
            preludeSet.add(CAL_Prelude_internal.Functions.divideLong.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.remainderLong.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.divideInt.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.remainderInt.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.divideByte.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.remainderByte.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.divideShort.getUnqualifiedName());  
            preludeSet.add(CAL_Prelude_internal.Functions.remainderShort.getUnqualifiedName());
            //////////////////////////////////////////////////////////////
                        
            preludeSet.add(CAL_Prelude_internal.Functions.compareInt.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.maxInt.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.minInt.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.absInt.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.signumInt.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.compareDouble.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.jCompareDouble.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.maxDouble.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.minDouble.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.absDouble.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.signumDouble.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.compareFloat.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.jCompareFloat.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.maxFloat.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.minFloat.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.absFloat.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.signumFloat.getUnqualifiedName());            
            preludeSet.add(CAL_Prelude_internal.Functions.compareLong.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.maxLong.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.minLong.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.absLong.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.signumLong.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.compareChar.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.maxChar.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.minChar.getUnqualifiedName());
            
            preludeSet.add(CAL_Prelude_internal.Functions.integer_ONE.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.integer_ZERO.getUnqualifiedName());
            // These Integer functions cannot be optimized since they are
            // foreign functions which act on a BigInteger object.  If the
            // object is null they can throw an exception.  A null Integer
            // cannot be directly created in CAL source, but it can be marshaled
            // from the result of a foreign function call.
            //preludeSet.add("maxInteger");
            //preludeSet.add("minInteger");
            //preludeSet.add("absInteger");
            //preludeSet.add("signumInteger");
            //preludeSet.add("addInteger");
            //preludeSet.add("subtractInteger");
            //preludeSet.add("multiplyInteger");
            //preludeSet.add("negateInteger");
            //preludeSet.add("longToInteger");
            //preludeSet.add("integerToString");
            //preludeSet.add("integerToInt");
            //preludeSet.add("integerToLong");        
            //preludeSet.add("appendString");        
            //preludeSet.add("integerToDouble");
          
            preludeSet.add(CAL_Prelude.Functions.round.getUnqualifiedName());
            preludeSet.add(CAL_Prelude.Functions.ceiling.getUnqualifiedName());
            preludeSet.add(CAL_Prelude.Functions.floor.getUnqualifiedName());         
            preludeSet.add(CAL_Prelude_internal.Functions.internal_algebraicValue_new.getUnqualifiedName());
            //Can't be optimized since AlgebraicValue may be null and hence we get an exception.
            //preludeSet.add("algebraicValue_getDataConstructorName");
            //preludeSet.add("algebraicValue_getNArguments");
            // Can't optimize algebraicValue_getNthArgument since it can throw an exception if
            // the argument number is not valid.
            //preludeSet.add("algebraicValue_getNthArgument");         
            preludeSet.add(CAL_Prelude_internal.Functions.jArrayList_new.getUnqualifiedName());
            // These two jList functions can throw an exception if the list is null.
            //preludeSet.add("jList_size");
            //preludeSet.add("jList_get");               
            preludeSet.add(CAL_Prelude.Functions.notANumber.getUnqualifiedName());
            preludeSet.add(CAL_Prelude.Functions.isNotANumber.getUnqualifiedName());
            preludeSet.add(CAL_Prelude_internal.Functions.jStringBuilder_new.getUnqualifiedName());
        }
        
        {
            final Set<String> bitsSet = new HashSet<String> ();
            eagerlyEvaluatedFunctions.put (CAL_Bits_internal.MODULE_NAME, Collections.unmodifiableSet(bitsSet));     
            
            bitsSet.add(CAL_Bits_internal.Functions.bitwiseAndInt.getUnqualifiedName());
            bitsSet.add(CAL_Bits_internal.Functions.bitwiseOrInt.getUnqualifiedName());
            bitsSet.add(CAL_Bits_internal.Functions.bitwiseXorInt.getUnqualifiedName());
            bitsSet.add(CAL_Bits_internal.Functions.complementInt.getUnqualifiedName());
            bitsSet.add(CAL_Bits_internal.Functions.shiftLInt.getUnqualifiedName());
            bitsSet.add(CAL_Bits_internal.Functions.shiftRInt.getUnqualifiedName());
            bitsSet.add(CAL_Bits_internal.Functions.shiftRUnsignedInt.getUnqualifiedName());
            bitsSet.add(CAL_Bits_internal.Functions.highestBitMaskInt.getUnqualifiedName());
            
            bitsSet.add(CAL_Bits_internal.Functions.bitwiseAndLong.getUnqualifiedName());
            bitsSet.add(CAL_Bits_internal.Functions.bitwiseOrLong.getUnqualifiedName());
            bitsSet.add(CAL_Bits_internal.Functions.bitwiseXorLong.getUnqualifiedName());
            bitsSet.add(CAL_Bits_internal.Functions.complementLong.getUnqualifiedName());
            bitsSet.add(CAL_Bits_internal.Functions.shiftLLong.getUnqualifiedName());
            bitsSet.add(CAL_Bits_internal.Functions.shiftRLong.getUnqualifiedName());
            bitsSet.add(CAL_Bits_internal.Functions.shiftRUnsignedLong.getUnqualifiedName());
            bitsSet.add(CAL_Bits_internal.Functions.highestBitMaskLong.getUnqualifiedName());                                   
        }
        
        {
            final Set<String> arrayPrimitivesSet = new HashSet<String> ();
            eagerlyEvaluatedFunctions.put (CAL_ArrayPrimitives_internal.MODULE_NAME, Collections.unmodifiableSet(arrayPrimitivesSet));        
            arrayPrimitivesSet.add(CAL_ArrayPrimitives_internal.Functions.charArray_length.getUnqualifiedName());
            arrayPrimitivesSet.add(CAL_ArrayPrimitives_internal.Functions.booleanArray_length.getUnqualifiedName());
            arrayPrimitivesSet.add(CAL_ArrayPrimitives_internal.Functions.byteArray_length.getUnqualifiedName());
            arrayPrimitivesSet.add(CAL_ArrayPrimitives_internal.Functions.shortArray_length.getUnqualifiedName());
            arrayPrimitivesSet.add(CAL_ArrayPrimitives_internal.Functions.intArray_length.getUnqualifiedName());
            arrayPrimitivesSet.add(CAL_ArrayPrimitives_internal.Functions.longArray_length.getUnqualifiedName());
            arrayPrimitivesSet.add(CAL_ArrayPrimitives_internal.Functions.floatArray_length.getUnqualifiedName());
            arrayPrimitivesSet.add(CAL_ArrayPrimitives_internal.Functions.doubleArray_length.getUnqualifiedName());
            arrayPrimitivesSet.add(CAL_ArrayPrimitives_internal.Functions.objectArray_length.getUnqualifiedName());
            arrayPrimitivesSet.add(CAL_ArrayPrimitives_internal.Functions.calValueArray_length.getUnqualifiedName());
        }
        
        {
            final Set<String> arraySet = new HashSet<String> ();
            eagerlyEvaluatedFunctions.put (CAL_Array.MODULE_NAME, Collections.unmodifiableSet(arraySet));
            arraySet.add(CAL_Array.Functions.length.getUnqualifiedName());
            arraySet.add(CAL_Array.Functions.array1.getUnqualifiedName());
            arraySet.add(CAL_Array.Functions.array2.getUnqualifiedName());
            arraySet.add(CAL_Array.Functions.array3.getUnqualifiedName());
            arraySet.add(CAL_Array.Functions.array4.getUnqualifiedName());
            arraySet.add(CAL_Array.Functions.array5.getUnqualifiedName());
            arraySet.add(CAL_Array.Functions.array6.getUnqualifiedName());
            arraySet.add(CAL_Array.Functions.array7.getUnqualifiedName());       
            // Note: Array.update and Array.subscript (and the type specific versions) cannot be optimized
            // because they can throw an exception if the array index is not valid.
        }

        {
            final Set<String> charSet = new HashSet<String> ();
            eagerlyEvaluatedFunctions.put(CAL_Char.MODULE_NAME, Collections.unmodifiableSet(charSet));
            charSet.add(CAL_Char.Functions.toInt.getUnqualifiedName());       
            charSet.add(CAL_Char.Functions.isSpaceChar.getUnqualifiedName());
            charSet.add(CAL_Char.Functions.isWhitespace.getUnqualifiedName());
            charSet.add(CAL_Char.Functions.isUpperCase.getUnqualifiedName());
            charSet.add(CAL_Char.Functions.isLowerCase.getUnqualifiedName());
            charSet.add(CAL_Char.Functions.isLetter.getUnqualifiedName());
            charSet.add(CAL_Char.Functions.isDigit.getUnqualifiedName());
            charSet.add(CAL_Char.Functions.isLetterOrDigit.getUnqualifiedName());
            charSet.add(CAL_Char.Functions.toUpperCase.getUnqualifiedName());
            charSet.add(CAL_Char.Functions.toLowerCase.getUnqualifiedName());
        }
        
        {
            final Set<String> mathSet = new HashSet<String>();
            eagerlyEvaluatedFunctions.put(CAL_Math.MODULE_NAME, Collections.unmodifiableSet(mathSet));        
            mathSet.add(CAL_Math.Functions.pi.getUnqualifiedName());
            mathSet.add(CAL_Math.Functions.exp.getUnqualifiedName());
            mathSet.add(CAL_Math.Functions.log.getUnqualifiedName());
            mathSet.add(CAL_Math.Functions.sqrt.getUnqualifiedName());
            mathSet.add(CAL_Math.Functions.power.getUnqualifiedName()); 
            mathSet.add(CAL_Math.Functions.logBase.getUnqualifiedName());
            mathSet.add(CAL_Math.Functions.sin.getUnqualifiedName());
            mathSet.add(CAL_Math.Functions.cos.getUnqualifiedName());
            mathSet.add(CAL_Math.Functions.tan.getUnqualifiedName());
            mathSet.add(CAL_Math.Functions.asin.getUnqualifiedName());
            mathSet.add(CAL_Math.Functions.acos.getUnqualifiedName());
            mathSet.add(CAL_Math.Functions.atan.getUnqualifiedName());
            mathSet.add(CAL_Math.Functions.atan2.getUnqualifiedName());
            mathSet.add(CAL_Math.Functions.sinh.getUnqualifiedName());
            mathSet.add(CAL_Math.Functions.cosh.getUnqualifiedName());
            mathSet.add(CAL_Math.Functions.tanh.getUnqualifiedName());
            mathSet.add(CAL_Math.Functions.asinh.getUnqualifiedName());
            mathSet.add(CAL_Math.Functions.acosh.getUnqualifiedName());
            mathSet.add(CAL_Math.Functions.atanh.getUnqualifiedName());  
            mathSet.add(CAL_Math.Functions.sqrt.getUnqualifiedName());
        }
        
        {
            final Set<String> recordSet = new HashSet<String>();
            eagerlyEvaluatedFunctions.put(CAL_Record_internal.MODULE_NAME, Collections.unmodifiableSet(recordSet));        

            recordSet.add(CAL_Record_internal.Functions.fieldNamesPrimitive.getUnqualifiedName());
            recordSet.add(CAL_Record_internal.Functions.hasFieldPrimitive.getUnqualifiedName());
        }
        
    }    
}
