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

package org.openquark.cal.internal.machine.lecc.functions;

import junit.framework.TestCase;

import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.internal.module.Cal.Core.CAL_Debug_internal;
import org.openquark.cal.internal.module.Cal.Core.CAL_Dynamic_internal;
import org.openquark.cal.internal.module.Cal.Core.CAL_Exception_internal;
import org.openquark.cal.internal.module.Cal.Core.CAL_Prelude_internal;
import org.openquark.cal.internal.module.Cal.Core.CAL_Record_internal;
import org.openquark.cal.internal.module.Cal.Utilities.CAL_QuickCheck_internal;
import org.openquark.cal.internal.runtime.lecc.RTSupercombinator;
import org.openquark.cal.internal.runtime.lecc.functions.RTAppendRecordPrimitive;
import org.openquark.cal.internal.runtime.lecc.functions.RTArbitraryRecordPrimitive;
import org.openquark.cal.internal.runtime.lecc.functions.RTCoArbitraryRecordPrimitive;
import org.openquark.cal.internal.runtime.lecc.functions.RTCompareRecord;
import org.openquark.cal.internal.runtime.lecc.functions.RTDeepSeq;
import org.openquark.cal.internal.runtime.lecc.functions.RTEqualsRecord;
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
import org.openquark.cal.module.Cal.Core.CAL_Prelude;

/**
 * Runtime lecc classes cannot refer to binding files, since we don't want to require
 * those classes (and the classes they reference such as SourceModel) as part of the
 * minimum runtime component. This class tests that the hard-coded string constants
 * match the binding file versions.
 * 
 * @author Bo Ilic
 */
public final class RuntimeStringConstantsTest extends TestCase {
       
    public RuntimeStringConstantsTest(String name) {
        super(name);
    }
    
    public void testRuntimeStringConstants() {
        
        testFunction(RTAppendRecordPrimitive.$instance,
            CAL_Dynamic_internal.Functions.appendRecordPrimitive);
        
        testFunction(RTArbitraryRecordPrimitive.$instance,
            CAL_QuickCheck_internal.Functions.arbitraryRecordPrimitive);
        
        testFunction(RTCoArbitraryRecordPrimitive.$instance,
            CAL_QuickCheck_internal.Functions.coarbitraryRecordPrimitive);
        
        testFunction(RTCompareRecord.$instance,
            CAL_Prelude_internal.Functions.compareRecord);
        
        testFunction(RTDeepSeq.$instance,
            CAL_Prelude.Functions.deepSeq);
        
        testFunction(RTEqualsRecord.$instance,
            CAL_Prelude_internal.Functions.equalsRecord);
        
        testFunction(RTError.$instance,
            CAL_Prelude.Functions.error);
        
        testFunction(RTInsertOrdinalRecordFieldPrimitive.$instance,
            CAL_Dynamic_internal.Functions.insertOrdinalRecordFieldPrimitive);
        
        testFunction(RTInsertTextualRecordFieldPrimitive.$instance,
            CAL_Dynamic_internal.Functions.insertTextualRecordFieldPrimitive);
        
        testFunction(RTNotEqualsRecord.$instance,
            CAL_Prelude_internal.Functions.notEqualsRecord);
        
        testFunction(RTOrdinalValue.$instance,
            CAL_Prelude_internal.Functions.ordinalValue);
        
        testFunction(RTPrimCatch.$instance,
            CAL_Exception_internal.Functions.primCatch);
        
        testFunction(RTPrimThrow.$instance,
            CAL_Exception_internal.Functions.primThrow);
        
        testFunction(RTRecordFieldTypePrimitive.$instance,
            CAL_Dynamic_internal.Functions.recordFieldTypePrimitive);
        
        testFunction(RTRecordFieldValuePrimitive.$instance,            
            CAL_Dynamic_internal.Functions.recordFieldValuePrimitive);
        
        testFunction(RTRecordFromJListPrimitive.$instance,
            CAL_Prelude_internal.Functions.recordFromJListPrimitive);
        
        testFunction(RTRecordFromJMapPrimitive.$instance,
            CAL_Prelude_internal.Functions.recordFromJMapPrimitive);
        
        testFunction(RTRecordToJListPrimitive.$instance,
            CAL_Prelude_internal.Functions.recordToJListPrimitive);
        
        testFunction(RTRecordToJRecordValuePrimitive.$instance,
            CAL_Record_internal.Functions.recordToJRecordValuePrimitive);
        
        testFunction(RTRecordTypeDictionary.$instance,
            CAL_Prelude_internal.Functions.recordTypeDictionary);
        
        testFunction(RTSeq.$instance,
            CAL_Prelude.Functions.seq);
        
        testFunction(RTShowRecord.$instance,
            CAL_Debug_internal.Functions.showRecord);
       
        testFunction(RTStrictRecordPrimitive.$instance,
            CAL_Record_internal.Functions.strictRecordPrimitive);
                
    }
    
    private void testFunction(final RTSupercombinator function, final QualifiedName functionName) {
        assertEquals(function.getModuleName(), functionName.getModuleName().toSourceText());
        assertEquals(function.getUnqualifiedName(), functionName.getUnqualifiedName());
        assertEquals(function.getQualifiedName(), functionName.getQualifiedName());        
    }
}
