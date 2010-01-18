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
 * JavaTypeNames.java
 * Creation date: Oct 18, 2006.
 * By: Edward Lam
 */
package org.openquark.cal.internal.machine.lecc;

import org.openquark.cal.internal.javamodel.JavaTypeName;
import org.openquark.cal.internal.runtime.RuntimeEnvironment;
import org.openquark.cal.internal.runtime.StandaloneRuntimeEnvironment;
import org.openquark.cal.internal.runtime.lecc.RTApplication;
import org.openquark.cal.internal.runtime.lecc.RTCAF;
import org.openquark.cal.internal.runtime.lecc.RTCons;
import org.openquark.cal.internal.runtime.lecc.RTData;
import org.openquark.cal.internal.runtime.lecc.RTDataConsFieldSelection;
import org.openquark.cal.internal.runtime.lecc.RTExecutionContext;
import org.openquark.cal.internal.runtime.lecc.RTForeignFunctionException;
import org.openquark.cal.internal.runtime.lecc.RTFullApp;
import org.openquark.cal.internal.runtime.lecc.RTFunction;
import org.openquark.cal.internal.runtime.lecc.RTIndirection;
import org.openquark.cal.internal.runtime.lecc.RTOApp2;
import org.openquark.cal.internal.runtime.lecc.RTOApp3;
import org.openquark.cal.internal.runtime.lecc.RTRecordExtension;
import org.openquark.cal.internal.runtime.lecc.RTRecordSelection;
import org.openquark.cal.internal.runtime.lecc.RTRecordUpdate;
import org.openquark.cal.internal.runtime.lecc.RTRecordValue;
import org.openquark.cal.internal.runtime.lecc.RTResultFunction;
import org.openquark.cal.internal.runtime.lecc.RTSupercombinator;
import org.openquark.cal.internal.runtime.lecc.RTValue;
import org.openquark.cal.internal.runtime.lecc.functions.RTAppendRecordPrimitive;
import org.openquark.cal.internal.runtime.lecc.functions.RTArbitraryRecordPrimitive;
import org.openquark.cal.internal.runtime.lecc.functions.RTBuildListPrimitive;
import org.openquark.cal.internal.runtime.lecc.functions.RTBuildRecordPrimitive;
import org.openquark.cal.internal.runtime.lecc.functions.RTCalFunction;
import org.openquark.cal.internal.runtime.lecc.functions.RTCalListIterator;
import org.openquark.cal.internal.runtime.lecc.functions.RTCoArbitraryRecordPrimitive;
import org.openquark.cal.internal.runtime.lecc.functions.RTComparator;
import org.openquark.cal.internal.runtime.lecc.functions.RTCompareRecord;
import org.openquark.cal.internal.runtime.lecc.functions.RTDeepSeq;
import org.openquark.cal.internal.runtime.lecc.functions.RTEqualsRecord;
import org.openquark.cal.internal.runtime.lecc.functions.RTEquivalenceRelation;
import org.openquark.cal.internal.runtime.lecc.functions.RTError;
import org.openquark.cal.internal.runtime.lecc.functions.RTInsertOrdinalRecordFieldPrimitive;
import org.openquark.cal.internal.runtime.lecc.functions.RTInsertTextualRecordFieldPrimitive;
import org.openquark.cal.internal.runtime.lecc.functions.RTNotEqualsRecord;
import org.openquark.cal.internal.runtime.lecc.functions.RTOrdinalValue;
import org.openquark.cal.internal.runtime.lecc.functions.RTPrimCatch;
import org.openquark.cal.internal.runtime.lecc.functions.RTPrimThrow;
import org.openquark.cal.internal.runtime.lecc.functions.RTRecordFieldTypePrimitive;
import org.openquark.cal.internal.runtime.lecc.functions.RTRecordFieldValuePrimitive;
import org.openquark.cal.internal.runtime.lecc.functions.RTRecordFromJListPrimitive;
import org.openquark.cal.internal.runtime.lecc.functions.RTRecordFromJMapPrimitive;
import org.openquark.cal.internal.runtime.lecc.functions.RTRecordToJListPrimitive;
import org.openquark.cal.internal.runtime.lecc.functions.RTRecordToJRecordValuePrimitive;
import org.openquark.cal.internal.runtime.lecc.functions.RTRecordTypeDictionary;
import org.openquark.cal.internal.runtime.lecc.functions.RTSeq;
import org.openquark.cal.internal.runtime.lecc.functions.RTShowRecord;
import org.openquark.cal.internal.runtime.lecc.functions.RTStrictRecordPrimitive;


/**
 * This class is simply a convenient holder for a number of 
 * JavaTypeName constants.
 * @author Raymond Cypher
 */
final class JavaTypeNames {

    //types used in the lecc implementation (either in the Java code in the lecc package, or in the generated 
    //Java classes. Effectively these are the runtime support classes needed by lecc.
    
    static final JavaTypeName RTFULLAPP = JavaTypeName.make(RTFullApp.class);
    static final JavaTypeName RTAPP1L = JavaTypeName.make(RTFullApp.General._1._L.class);
    static final JavaTypeName RTAPP1S = JavaTypeName.make(RTFullApp.General._1._S.class);
    static final JavaTypeName RTAPP2L = JavaTypeName.make(RTFullApp.General._2._L.class);
    static final JavaTypeName RTAPP2S = JavaTypeName.make(RTFullApp.General._2._S.class);
    static final JavaTypeName RTAPP3L = JavaTypeName.make(RTFullApp.General._3._L.class);
    static final JavaTypeName RTAPP3S = JavaTypeName.make(RTFullApp.General._3._S.class);
    static final JavaTypeName RTAPP4L = JavaTypeName.make(RTFullApp.General._4._L.class);
    static final JavaTypeName RTAPP4S = JavaTypeName.make(RTFullApp.General._4._S.class);
    static final JavaTypeName RTAPP5L = JavaTypeName.make(RTFullApp.General._5._L.class);
    static final JavaTypeName RTAPP5S = JavaTypeName.make(RTFullApp.General._5._S.class);
    static final JavaTypeName RTAPP6L = JavaTypeName.make(RTFullApp.General._6._L.class);
    static final JavaTypeName RTAPP6S = JavaTypeName.make(RTFullApp.General._6._S.class);
    static final JavaTypeName RTAPP7L = JavaTypeName.make(RTFullApp.General._7._L.class);
    static final JavaTypeName RTAPP7S = JavaTypeName.make(RTFullApp.General._7._S.class);
    static final JavaTypeName RTAPP8L = JavaTypeName.make(RTFullApp.General._8._L.class);
    static final JavaTypeName RTAPP8S = JavaTypeName.make(RTFullApp.General._8._S.class);
    static final JavaTypeName RTAPP9L = JavaTypeName.make(RTFullApp.General._9._L.class);
    static final JavaTypeName RTAPP9S = JavaTypeName.make(RTFullApp.General._9._S.class);
    static final JavaTypeName RTAPP10L = JavaTypeName.make(RTFullApp.General._10._L.class);
    static final JavaTypeName RTAPP10S = JavaTypeName.make(RTFullApp.General._10._S.class);
    static final JavaTypeName RTAPP11L = JavaTypeName.make(RTFullApp.General._11._L.class);
    static final JavaTypeName RTAPP11S = JavaTypeName.make(RTFullApp.General._11._S.class);
    static final JavaTypeName RTAPP12L = JavaTypeName.make(RTFullApp.General._12._L.class);
    static final JavaTypeName RTAPP12S = JavaTypeName.make(RTFullApp.General._12._S.class);
    static final JavaTypeName RTAPP13L = JavaTypeName.make(RTFullApp.General._13._L.class);
    static final JavaTypeName RTAPP13S = JavaTypeName.make(RTFullApp.General._13._S.class);
    static final JavaTypeName RTAPP14L = JavaTypeName.make(RTFullApp.General._14._L.class);
    static final JavaTypeName RTAPP14S = JavaTypeName.make(RTFullApp.General._14._S.class);
    static final JavaTypeName RTAPP15L = JavaTypeName.make(RTFullApp.General._15._L.class);
    static final JavaTypeName RTAPP15S = JavaTypeName.make(RTFullApp.General._15._S.class);
    
    static final JavaTypeName RTAPPLICATION = JavaTypeName.make(RTApplication.class);
    static final JavaTypeName RTCAF = JavaTypeName.make(RTCAF.class);      
    static final JavaTypeName RTCONS = JavaTypeName.make(RTCons.class);
    static final JavaTypeName RTEXECUTION_CONTEXT = JavaTypeName.make(RTExecutionContext.class);
    static final JavaTypeName RUNTIME_ENIVONMENT = JavaTypeName.make(RuntimeEnvironment.class);
    static final JavaTypeName STANDALONE_RUNTIME_ENVIRONMENT = JavaTypeName.make(StandaloneRuntimeEnvironment.class);    
    static final JavaTypeName RTFOREIGN_FUNCTION_EXCEPTION = JavaTypeName.make(RTForeignFunctionException.class);
    static final JavaTypeName RTFUNCTION = JavaTypeName.make(RTFunction.class);
    static final JavaTypeName RTINDIRECTION = JavaTypeName.make(RTIndirection.class);
    static final JavaTypeName RTDATA_BOOLEAN = JavaTypeName.make(RTData.CAL_Boolean.class);
    static final JavaTypeName RTDATA_BOOLEAN_FALSE = JavaTypeName.make(RTData.CAL_Boolean.CAL_False.class);
    static final JavaTypeName RTDATA_BYTE = JavaTypeName.make(RTData.CAL_Byte.class);
    static final JavaTypeName RTDATA_CHAR = JavaTypeName.make(RTData.CAL_Char.class);
    static final JavaTypeName RTDATA_DOUBLE = JavaTypeName.make(RTData.CAL_Double.class);
    static final JavaTypeName RTDATA_FLOAT = JavaTypeName.make(RTData.CAL_Float.class);
    static final JavaTypeName RTDATA_INT = JavaTypeName.make(RTData.CAL_Int.class);
    static final JavaTypeName RTDATA_LONG = JavaTypeName.make(RTData.CAL_Long.class);
    static final JavaTypeName RTDATA_SHORT = JavaTypeName.make(RTData.CAL_Short.class);
    static final JavaTypeName RTDATA_STRING = JavaTypeName.make(RTData.CAL_String.class);
    static final JavaTypeName RTDATA_INTEGER = JavaTypeName.make(RTData.CAL_Integer.class);
    static final JavaTypeName RTDATA_OPAQUE = JavaTypeName.make(RTData.CAL_Opaque.class);
    static final JavaTypeName RTDATACONS_FIELD_SELECTION = JavaTypeName.make(RTDataConsFieldSelection.class);
    static final JavaTypeName RTOAPP2 = JavaTypeName.make(RTOApp2.class);
    static final JavaTypeName RTOAPP3 = JavaTypeName.make(RTOApp3.class);
    static final JavaTypeName RTRECORD_UPDATE = JavaTypeName.make(RTRecordUpdate.class);
    static final JavaTypeName RTRECORD_EXTENSION = JavaTypeName.make(RTRecordExtension.class);
    static final JavaTypeName RTRECORD_SELECTION = JavaTypeName.make(RTRecordSelection.class);
    static final JavaTypeName RTRECORD_SELECTION_ORDINAL_FIELD = JavaTypeName.make(RTRecordSelection.Ordinal.class);
    static final JavaTypeName RTRECORD_SELECTION_TEXTUAL_FIELD = JavaTypeName.make(RTRecordSelection.Textual.class);
    static final JavaTypeName RTRECORD_VALUE = JavaTypeName.make(RTRecordValue.class);
    static final JavaTypeName RTRESULT_FUNCTION = JavaTypeName.make(RTResultFunction.class);    
    static final JavaTypeName RTSUPERCOMBINATOR = JavaTypeName.make(RTSupercombinator.class);
    static final JavaTypeName RTVALUE = JavaTypeName.make(RTValue.class);
    static final JavaTypeName RTVALUE_ARRAY = JavaTypeName.make(RTValue[].class);      
    
    static final JavaTypeName RTCAL_LIST_ITERATOR = JavaTypeName.make(RTCalListIterator.class);
    static final JavaTypeName RTCAL_FUNCTION = JavaTypeName.make(RTCalFunction.class);
    static final JavaTypeName RTCOMPARATOR = JavaTypeName.make(RTComparator.class);
    static final JavaTypeName RTCOMPARE_RECORD = JavaTypeName.make(RTCompareRecord.class);
    static final JavaTypeName RTEQUALS_RECORD = JavaTypeName.make(RTEqualsRecord.class);
    static final JavaTypeName RTEQUIVALENCE_RELATION = JavaTypeName.make(RTEquivalenceRelation.class);
    static final JavaTypeName RTRECORD_FROM_JLIST_PRIMITIVE = JavaTypeName.make(RTRecordFromJListPrimitive.class);
    static final JavaTypeName RTNOT_EQUALS_RECORD = JavaTypeName.make(RTNotEqualsRecord.class);
    static final JavaTypeName RTRECORD_TO_JLIST_PRIMITIVE = JavaTypeName.make(RTRecordToJListPrimitive.class);
    static final JavaTypeName RTRECORD_TO_JRECORDVALUE_PRIMITIVE = JavaTypeName.make(RTRecordToJRecordValuePrimitive.class);
    static final JavaTypeName RTSTRICT_RECORD_PRIMITIVE = JavaTypeName.make(RTStrictRecordPrimitive.class);
    static final JavaTypeName RTRECORD_FROM_JMAP_PRIMITIVE = JavaTypeName.make(RTRecordFromJMapPrimitive.class);
    static final JavaTypeName RTRECORD_FIELD_VALUE_PRIMITIVE = JavaTypeName.make(RTRecordFieldValuePrimitive.class);
    static final JavaTypeName RTRECORD_FIELD_TYPE_PRIMITIVE = JavaTypeName.make(RTRecordFieldTypePrimitive.class);
    static final JavaTypeName RTAPPEND_RECORD_PRIMITIVE = JavaTypeName.make(RTAppendRecordPrimitive.class);
    static final JavaTypeName RTARBITRARY_RECORD_PRIMITIVE = JavaTypeName.make(RTArbitraryRecordPrimitive.class);
    static final JavaTypeName RTCOARBITRARY_RECORD_PRIMITIVE = JavaTypeName.make(RTCoArbitraryRecordPrimitive.class);
    static final JavaTypeName RTINSERT_ORDINAL_RECORD_FIELD_PRIMITIVE = JavaTypeName.make(RTInsertOrdinalRecordFieldPrimitive.class);
    static final JavaTypeName RTINSERT_TEXTUAL_RECORD_FIELD_PRIMITIVE = JavaTypeName.make(RTInsertTextualRecordFieldPrimitive.class);
    static final JavaTypeName RTRECORD_TYPE_DICTIONARY = JavaTypeName.make(RTRecordTypeDictionary.class);
   
    static final JavaTypeName RTLIST_TO_RECORD_PRIMITIVE = JavaTypeName.make(RTBuildRecordPrimitive.class);
    static final JavaTypeName RTRECORD_TO_LIST_PRIMITIVE = JavaTypeName.make(RTBuildListPrimitive.class);
    
    static final JavaTypeName RTTHROW = JavaTypeName.make(RTPrimThrow.class);
    static final JavaTypeName RTCATCH = JavaTypeName.make(RTPrimCatch.class);
    static final JavaTypeName RTSHOW_RECORD = JavaTypeName.make(RTShowRecord.class);
    static final JavaTypeName RTSEQ = JavaTypeName.make(RTSeq.class);
    static final JavaTypeName RTDEEP_SEQ = JavaTypeName.make(RTDeepSeq.class);
    static final JavaTypeName RTORDINAL_VALUE = JavaTypeName.make(RTOrdinalValue.class);
    static final JavaTypeName RTERROR = JavaTypeName.make(RTError.class);
    
    /**
     * Private default constructor to prevent 
     * instantiation.
     */
    private JavaTypeNames () {}
}
