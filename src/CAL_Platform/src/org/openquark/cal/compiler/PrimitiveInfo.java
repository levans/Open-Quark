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
 * PrimitiveInfo.java
 * Created: Mar 31, 2005
 * By: Bo Ilic
 */

package org.openquark.cal.compiler;

import java.util.HashSet;
import java.util.Set;

import org.openquark.cal.internal.module.Cal.Collections.CAL_List_internal;
import org.openquark.cal.internal.module.Cal.Core.CAL_Bits_internal;
import org.openquark.cal.internal.module.Cal.Core.CAL_Debug_internal;
import org.openquark.cal.internal.module.Cal.Core.CAL_Dynamic_internal;
import org.openquark.cal.internal.module.Cal.Core.CAL_Exception_internal;
import org.openquark.cal.internal.module.Cal.Core.CAL_Prelude_internal;
import org.openquark.cal.internal.module.Cal.Core.CAL_Record_internal;
import org.openquark.cal.internal.module.Cal.Utilities.CAL_QuickCheck_internal;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;


/**
 * Contains info on the primitive functions and types that can be defined in CAL.
 * 
 * Currently this contains:
 * - a simple list intended to give early compiler error messages if someone attempts
 * to declare an unsupported primitive function. 
 * 
 * @author Bo Ilic
 */
final class PrimitiveInfo {
       
    /** QualifiedName set */
    private static final Set<QualifiedName> primitiveFunctions = new HashSet<QualifiedName>();
    
    static {        
        addBuiltInFunction(CAL_Prelude_internal.Functions.equalsInt);
        addBuiltInFunction(CAL_Prelude_internal.Functions.notEqualsInt);
        addBuiltInFunction(CAL_Prelude_internal.Functions.greaterThanInt);
        addBuiltInFunction(CAL_Prelude_internal.Functions.greaterThanEqualsInt);
        addBuiltInFunction(CAL_Prelude_internal.Functions.lessThanInt); 
        addBuiltInFunction(CAL_Prelude_internal.Functions.lessThanEqualsInt);

        addBuiltInFunction(CAL_Prelude_internal.Functions.addInt);
        addBuiltInFunction(CAL_Prelude_internal.Functions.subtractInt);
        addBuiltInFunction(CAL_Prelude_internal.Functions.multiplyInt);
        addBuiltInFunction(CAL_Prelude_internal.Functions.divideInt);
        addBuiltInFunction(CAL_Prelude_internal.Functions.negateInt);
        addBuiltInFunction(CAL_Prelude_internal.Functions.remainderInt);

        //primitive functions for comparison and arithmetic with floats

        addBuiltInFunction(CAL_Prelude_internal.Functions.equalsFloat);
        addBuiltInFunction(CAL_Prelude_internal.Functions.notEqualsFloat);
        addBuiltInFunction(CAL_Prelude_internal.Functions.greaterThanFloat);
        addBuiltInFunction(CAL_Prelude_internal.Functions.greaterThanEqualsFloat);
        addBuiltInFunction(CAL_Prelude_internal.Functions.lessThanFloat);
        addBuiltInFunction(CAL_Prelude_internal.Functions.lessThanEqualsFloat);

        addBuiltInFunction(CAL_Prelude_internal.Functions.addFloat);
        addBuiltInFunction(CAL_Prelude_internal.Functions.subtractFloat);
        addBuiltInFunction(CAL_Prelude_internal.Functions.multiplyFloat);
        addBuiltInFunction(CAL_Prelude_internal.Functions.divideFloat);
        addBuiltInFunction(CAL_Prelude_internal.Functions.negateFloat);
        addBuiltInFunction(CAL_Prelude_internal.Functions.remainderFloat);
                       
        //primitive functions for comparison and arithmetic with doubles

        addBuiltInFunction(CAL_Prelude_internal.Functions.equalsDouble);
        addBuiltInFunction(CAL_Prelude_internal.Functions.notEqualsDouble);
        addBuiltInFunction(CAL_Prelude_internal.Functions.greaterThanDouble);
        addBuiltInFunction(CAL_Prelude_internal.Functions.greaterThanEqualsDouble);
        addBuiltInFunction(CAL_Prelude_internal.Functions.lessThanDouble);
        addBuiltInFunction(CAL_Prelude_internal.Functions.lessThanEqualsDouble);

        addBuiltInFunction(CAL_Prelude_internal.Functions.addDouble);
        addBuiltInFunction(CAL_Prelude_internal.Functions.subtractDouble);
        addBuiltInFunction(CAL_Prelude_internal.Functions.multiplyDouble);
        addBuiltInFunction(CAL_Prelude_internal.Functions.divideDouble);
        addBuiltInFunction(CAL_Prelude_internal.Functions.negateDouble);
        addBuiltInFunction(CAL_Prelude_internal.Functions.remainderDouble);
        
        // primitive functions for comparison and arithmetic with longs

        addBuiltInFunction(CAL_Prelude_internal.Functions.equalsLong);
        addBuiltInFunction(CAL_Prelude_internal.Functions.notEqualsLong);
        addBuiltInFunction(CAL_Prelude_internal.Functions.greaterThanLong);
        addBuiltInFunction(CAL_Prelude_internal.Functions.greaterThanEqualsLong);
        addBuiltInFunction(CAL_Prelude_internal.Functions.lessThanLong);
        addBuiltInFunction(CAL_Prelude_internal.Functions.lessThanEqualsLong);

        addBuiltInFunction(CAL_Prelude_internal.Functions.addLong);
        addBuiltInFunction(CAL_Prelude_internal.Functions.subtractLong);
        addBuiltInFunction(CAL_Prelude_internal.Functions.multiplyLong);
        addBuiltInFunction(CAL_Prelude_internal.Functions.divideLong);
        addBuiltInFunction(CAL_Prelude_internal.Functions.negateLong); 
        addBuiltInFunction(CAL_Prelude_internal.Functions.remainderLong);

        // primitive functions for comparison with shorts

        addBuiltInFunction(CAL_Prelude_internal.Functions.equalsShort);
        addBuiltInFunction(CAL_Prelude_internal.Functions.notEqualsShort);
        addBuiltInFunction(CAL_Prelude_internal.Functions.greaterThanShort);
        addBuiltInFunction(CAL_Prelude_internal.Functions.greaterThanEqualsShort);
        addBuiltInFunction(CAL_Prelude_internal.Functions.lessThanShort);
        addBuiltInFunction(CAL_Prelude_internal.Functions.lessThanEqualsShort);

        // primitive functions for comparison with bytes
        addBuiltInFunction(CAL_Prelude_internal.Functions.equalsByte);
        addBuiltInFunction(CAL_Prelude_internal.Functions.notEqualsByte);
        addBuiltInFunction(CAL_Prelude_internal.Functions.greaterThanByte);
        addBuiltInFunction(CAL_Prelude_internal.Functions.greaterThanEqualsByte);
        addBuiltInFunction(CAL_Prelude_internal.Functions.lessThanByte);
        addBuiltInFunction(CAL_Prelude_internal.Functions.lessThanEqualsByte);             

        addBuiltInFunction(CAL_Prelude.Functions.error);

        //primitive functions for char comparison

        addBuiltInFunction(CAL_Prelude_internal.Functions.equalsChar);
        addBuiltInFunction(CAL_Prelude_internal.Functions.notEqualsChar);
        addBuiltInFunction(CAL_Prelude_internal.Functions.greaterThanChar);
        addBuiltInFunction(CAL_Prelude_internal.Functions.greaterThanEqualsChar);
        addBuiltInFunction(CAL_Prelude_internal.Functions.lessThanChar);
        addBuiltInFunction(CAL_Prelude_internal.Functions.lessThanEqualsChar);

        //primitive sequencing function
        addBuiltInFunction(CAL_Prelude.Functions.seq);

        //record functions
        addBuiltInFunction(CAL_Record_internal.Functions.fieldNamesPrimitive);
        addBuiltInFunction(CAL_Record_internal.Functions.hasFieldPrimitive);
        addBuiltInFunction(CAL_Dynamic_internal.Functions.fieldValuesPrimitive);        
        addBuiltInFunction(CAL_Dynamic_internal.Functions.recordFieldIndex);
        addBuiltInFunction(CAL_Dynamic_internal.Functions.recordFieldValuePrimitive);
        addBuiltInFunction(CAL_Dynamic_internal.Functions.recordFieldTypePrimitive);
        addBuiltInFunction(CAL_Dynamic_internal.Functions.insertTextualRecordFieldPrimitive);
        addBuiltInFunction(CAL_Dynamic_internal.Functions.insertOrdinalRecordFieldPrimitive);
        addBuiltInFunction(CAL_Dynamic_internal.Functions.appendRecordPrimitive);
        
        addBuiltInFunction(CAL_Prelude_internal.Functions.objectToCalValue);
        addBuiltInFunction(CAL_Prelude_internal.Functions.calValueToObject);
        addBuiltInFunction(CAL_Prelude_internal.Functions.makeComparator);
        addBuiltInFunction(CAL_Prelude_internal.Functions.makeEquivalenceRelation);
        addBuiltInFunction(CAL_Prelude.Functions.makeCalFunction);
               
        addBuiltInFunction(CAL_Prelude_internal.Functions.equalsRecord);
        addBuiltInFunction(CAL_Prelude_internal.Functions.notEqualsRecord);
        addBuiltInFunction(CAL_Prelude_internal.Functions.recordFromJListPrimitive);
        addBuiltInFunction(CAL_Prelude_internal.Functions.recordFromJMapPrimitive);
        addBuiltInFunction(CAL_Prelude_internal.Functions.recordToJListPrimitive);
        addBuiltInFunction(CAL_Record_internal.Functions.recordToJRecordValuePrimitive);        
        addBuiltInFunction(CAL_Record_internal.Functions.strictRecordPrimitive);   
        addBuiltInFunction(CAL_Prelude_internal.Functions.compareRecord);
        addBuiltInFunction(CAL_Prelude_internal.Functions.recordTypeDictionary);
        
        addBuiltInFunction(CAL_Prelude.Functions.unsafeCoerce);
                       
        addBuiltInFunction(CAL_Prelude.Functions.deepSeq);
        addBuiltInFunction(CAL_Prelude.Functions.error);
        
        addBuiltInFunction(CAL_Prelude_internal.Functions.executionContext); 
        
        addBuiltInFunction(CAL_Prelude_internal.Functions.ordinalValue);       
        
        addBuiltInFunction(CAL_Prelude.Functions.eager);
        
        addBuiltInFunction(CAL_List_internal.Functions.makeIterator);
        
        addBuiltInFunction(CAL_QuickCheck_internal.Functions.arbitraryRecordPrimitive);
        addBuiltInFunction(CAL_QuickCheck_internal.Functions.coarbitraryRecordPrimitive);
        
        addBuiltInFunction(CAL_Record_internal.Functions.buildListPrimitive);   
        addBuiltInFunction(CAL_Record_internal.Functions.buildRecordPrimitive);   
              
        addBuiltInFunction(CAL_Exception_internal.Functions.primThrow);
        addBuiltInFunction(CAL_Exception_internal.Functions.primCatch);
                      
        addBuiltInFunction(CAL_Debug_internal.Functions.showRecord);
        
        addBuiltInFunction(CAL_Bits_internal.Functions.bitwiseAndInt);
        addBuiltInFunction(CAL_Bits_internal.Functions.bitwiseOrInt);
        addBuiltInFunction(CAL_Bits_internal.Functions.bitwiseXorInt);
        addBuiltInFunction(CAL_Bits_internal.Functions.complementInt);
        addBuiltInFunction(CAL_Bits_internal.Functions.highestBitMaskInt);
        addBuiltInFunction(CAL_Bits_internal.Functions.shiftLInt);
        addBuiltInFunction(CAL_Bits_internal.Functions.shiftRInt);
        addBuiltInFunction(CAL_Bits_internal.Functions.shiftRUnsignedInt);
      
        addBuiltInFunction(CAL_Bits_internal.Functions.bitwiseAndLong);
        addBuiltInFunction(CAL_Bits_internal.Functions.bitwiseOrLong);
        addBuiltInFunction(CAL_Bits_internal.Functions.bitwiseXorLong);
        addBuiltInFunction(CAL_Bits_internal.Functions.complementLong);
        addBuiltInFunction(CAL_Bits_internal.Functions.highestBitMaskLong);
        addBuiltInFunction(CAL_Bits_internal.Functions.shiftLLong);
        addBuiltInFunction(CAL_Bits_internal.Functions.shiftRLong);
        addBuiltInFunction(CAL_Bits_internal.Functions.shiftRUnsignedLong);
        
    }
        
    static private void addBuiltInFunction(QualifiedName functionName) {
        primitiveFunctions.add(functionName);
    }
            
    /**     
     * @param functionName
     * @return true if functionName is the name of a supported primitive function.
     */
    static boolean isPrimitiveFunction(QualifiedName functionName) {
        return primitiveFunctions.contains(functionName);        
    }
       

    private PrimitiveInfo() {}
}
