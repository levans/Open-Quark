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
 * NRecordFromJListPrimitive.java
 * Created: Jul 28, 2005 
 * By: Raymond Cypher 
 */

package org.openquark.cal.internal.machine.g.functions;

import java.util.Iterator;
import java.util.List;

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
 * Implementation of Prelude.recordFromJListPrimitive :: Inputable r => JList -> {r};
 */
public final class NRecordFromJListPrimitive extends NPrimitiveFunc {

    public static final QualifiedName name = CAL_Prelude_internal.Functions.recordFromJListPrimitive;
    public static final NRecordFromJListPrimitive instance = new NRecordFromJListPrimitive ();
    
    private boolean implementationChecked = false;
    
    
    private NRecordFromJListPrimitive () { /* Constructor made private for creation control. */}
       
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
    public Node doEvaluation (Node[] arguments, Executor executor)
            throws CALExecutorException {

        checkImplementation(executor);
        
        // The two arguments in evaluated form will be on top of the stack, in reverse order.
        NRecordValue recordDictionary = (NRecordValue) executor.internalEvaluate(arguments[0]);
        List<?> inputList = (List<?>)((NValObject)executor.internalEvaluate(arguments[1])).getValue();
        //we use an iterator since inputList may be a non-random access list such as a java.util.LinkedList.
        Iterator<?> inputListIterator = inputList.iterator();
        
        List<String> fieldNames = recordDictionary.fieldNames();        
        int nFields = fieldNames.size();        
        
        //check that the number of fields in the inputList is the same as the number of fields in the record.
        //without this check it is possible that inputList could have more elements than the size of the record and still succeed.
        //This would still "work" but this check is useful to alert clients to potential bugs in their code.
        if (nFields != inputList.size()) {
            throw new IllegalArgumentException("A Java list of size " + inputList.size() + " cannot be input to a record with " + nFields + " fields.");
        }
        
        // Create a record value.
        NRecordValue newRecord = new NRecordValue (nFields);
        
        // The order of iteration over the fields is unimportant in this case, since there is no
        // evaluation being done.  We're just pushing suspensions into the NRecordValue node.
        for (int i = 0; i < nFields; ++i) {
            String fieldName = fieldNames.get(i);
            newRecord.putValue (fieldName, recordDictionary.getValue(fieldName).apply (new NValObject(inputListIterator.next())));
        }
        
        return newRecord;
    }

    private void checkImplementation (Executor executor) {
        if(!implementationChecked) {
            if (!isSingleMethodRootClass(executor, CAL_Prelude.TypeClasses.Inputable)) {            
                throw new IllegalStateException("The implementation of " + CAL_Prelude_internal.Functions.recordFromJListPrimitive.getQualifiedName() + " assumes that the class " + CAL_Prelude.TypeClasses.Inputable.getQualifiedName() + " has no superclasses and only 1 class method.");
            }
            implementationChecked = true;
        }
    }
}
