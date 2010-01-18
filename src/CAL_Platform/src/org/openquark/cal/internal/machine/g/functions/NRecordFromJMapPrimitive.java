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
 * NRecordFromJMapPrimitive.java
 * Created: Jan 12, 2007
 * By: Bo Ilic
 */

package org.openquark.cal.internal.machine.g.functions;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.openquark.cal.compiler.FieldName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.internal.machine.g.Executor;
import org.openquark.cal.internal.machine.g.NPrimitiveFunc;
import org.openquark.cal.internal.machine.g.NRecordValue;
import org.openquark.cal.internal.machine.g.NValObject;
import org.openquark.cal.internal.machine.g.Node;
import org.openquark.cal.internal.module.Cal.Core.CAL_Prelude_internal;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.runtime.CALExecutorException;


/**
 * Implementation of Prelude.recordFromJMapPrimitive :: Inputable r => JMap -> {r};
 */
public final class NRecordFromJMapPrimitive extends NPrimitiveFunc {

    public static final QualifiedName name = CAL_Prelude_internal.Functions.recordFromJMapPrimitive;
    public static final NRecordFromJMapPrimitive instance = new NRecordFromJMapPrimitive ();
    
    private boolean inputRecordImplementationChecked = false;
    
    
    private NRecordFromJMapPrimitive () { /* Constructor made private for creation control. */}
        
    @Override
    protected int getArity () {
        return 2;
    }
    
    @Override
    protected QualifiedName getName () {
        return name;
    }

    /**
     * Convert a java representation of a record into an internal record.
     */
    @Override
    public Node doEvaluation (final Node[] arguments, final Executor executor)
            throws CALExecutorException {

        checkInputRecordImplementation(executor);
        
        // The two arguments in evaluated form will be on top of the stack, in reverse order.
        final NRecordValue recordDictionary = (NRecordValue) executor.internalEvaluate(arguments[0]);
                              
        //inputMap, but with the keys sorted in FieldName order.
        final SortedMap<?, ?> fieldNameSortedInputMap;
        {
            final Map<?, ?> inputMap = (Map<?, ?>)((NValObject)executor.internalEvaluate(arguments[1])).getValue();       
                
            //In the case when inputMap is in fact a SortedMap that is using the comparator on the keys, then we can just use it directly.
            //We still need to verify that the keys are in fact FieldNames, but that will be done later.  
            //Otherwise we need to copy the map to get a proper iteration order.            
            if (inputMap instanceof SortedMap && ((SortedMap<?, ?>)inputMap).comparator() == null) {            
                fieldNameSortedInputMap = (SortedMap<?, ?>)inputMap;            
            } else {
                fieldNameSortedInputMap = new TreeMap<Object, Object>(inputMap);
            }
        }
        
        final List<String> fieldNames = recordDictionary.fieldNames();        
        final int nFields = fieldNames.size();        
        
        //check that the number of fields in the input map is the same as the number of fields in the record.
        //without this check it is possible that input map could have more elements than the size of the record and still succeed.
        //This would still "work" but this check is useful to alert clients to potential bugs in their code.
        if (nFields != fieldNameSortedInputMap.size()) {
            throw new IllegalArgumentException("A Java list of size " + fieldNameSortedInputMap.size() + " cannot be input to a record with " + nFields + " fields.");
        }
        
                                       
        final Iterator<?> iterator = fieldNameSortedInputMap.entrySet().iterator();
        
        // Create a record value.
        final NRecordValue newRecord = new NRecordValue (nFields);
        
        // The order of iteration over the fields is only import to check that fieldnames from the input map and from the record dictionary
        //(i.e. the CAL type) are the same. Evaluation is not being done.  We're just pushing suspensions into the NRecordValue node.
        for (int i = 0; i < nFields; ++i) {
            final Map.Entry<?, ?> entry = (Map.Entry<?, ?>)iterator.next();
            final String fieldNameFromInputMap = ((FieldName)entry.getKey()).getCalSourceForm();                              
            final String fieldName = fieldNames.get(i);
            if (!fieldNameFromInputMap.equals(fieldName)) {
                throw new IllegalArgumentException("The field names of the input map and target record must match exactly.");
            }            
            newRecord.putValue (fieldName, recordDictionary.getValue(fieldName).apply (new NValObject(entry.getValue())));
        }
        
        return newRecord;
    }

    private void checkInputRecordImplementation (Executor executor) {
        if(!inputRecordImplementationChecked) {
            if (!isSingleMethodRootClass(executor, CAL_Prelude.TypeClasses.Inputable)) {            
                throw new IllegalStateException("The implementation of " + CAL_Prelude_internal.Functions.recordFromJMapPrimitive.getQualifiedName() + " assumes that the class " + CAL_Prelude.TypeClasses.Inputable.getQualifiedName() + " has no superclasses and only 1 class method.");
            }
            inputRecordImplementationChecked = true;
        }
    }
    
  
}

