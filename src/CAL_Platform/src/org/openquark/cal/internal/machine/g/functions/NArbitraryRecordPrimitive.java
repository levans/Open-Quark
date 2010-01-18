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
 * NRTArbitraryRecordPrimitive.java
 * Creation date: Oct 3, 2006.
 * By: Magnus Byne
 */
package org.openquark.cal.internal.machine.g.functions;

import java.util.List;

import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.internal.machine.g.Executor;
import org.openquark.cal.internal.machine.g.NPrimitiveFunc;
import org.openquark.cal.internal.machine.g.NRecordValue;
import org.openquark.cal.internal.machine.g.NValInt;
import org.openquark.cal.internal.machine.g.Node;
import org.openquark.cal.internal.module.Cal.Utilities.CAL_QuickCheck_internal;
import org.openquark.cal.runtime.CALExecutorException;


/**
 * Implements the built-in primitive function:
 * arbitraryRecordPrimitive :: Arbitrary r => GenParams -> (GenParams -> GenParams) -> {r} 
 * for the G machine.
 * 
 * @author Magnus Byne
 */
public class NArbitraryRecordPrimitive extends NPrimitiveFunc {

    public static final QualifiedName name = CAL_QuickCheck_internal.Functions.arbitraryRecordPrimitive;
    public static final NArbitraryRecordPrimitive instance = new NArbitraryRecordPrimitive ();
       

    private NValInt indexOfGenerateClassMethod = null;
   
    
    private NArbitraryRecordPrimitive () {/* Constructor made private to control creation. */ }
        
    @Override
    protected int getArity () {
        return 3;
    }
   
    @Override
    protected QualifiedName getName () {
        return name;
    }
   
    @Override
    public Node doEvaluation (Node[] arguments, Executor executor)
            throws CALExecutorException {
       
        // Evaluate the 3 arguments.
        NRecordValue recordDictionary = (NRecordValue) executor.internalEvaluate(arguments[0]);
        Node genParams = executor.internalEvaluate(arguments[1]);
        Node independent = executor.internalEvaluate(arguments[2]);
        
        NValInt arbitraryMethodIndex = getIndexOfGenerateClassMethod(executor);
       
        List<String> fieldNames = recordDictionary.fieldNames();
        final int nFields = fieldNames.size();

        NRecordValue result=new NRecordValue(nFields);

        /*
         * Loops through all fields in the record dictionary and 
         * invokes the arbitrary class method to 
         * construct each record field
         */
        for (int i = 0; i < nFields; ++i) {
            genParams=executor.internalEvaluate(independent.apply( new NValInt(i)).apply(genParams));
            
            String fieldName = fieldNames.get(i);
            Node valueDictionaryThunk = recordDictionary.getValue(fieldName);
            
            result= result.insertRecordField(fieldName, 
                executor.internalEvaluate(valueDictionaryThunk.apply(arbitraryMethodIndex).apply(genParams)));
        }
        
        return result;
    }

    /**
     * Retrieve the current index of the arbitrary class method.
     * @param executor
     * @return - NValInt holding the index.
     */
    private final NValInt getIndexOfGenerateClassMethod (Executor executor) {
        if (indexOfGenerateClassMethod == null) {
            final int index = classMethodDictionaryIndex(executor, CAL_QuickCheck_internal.Functions.generateInstance);             
            indexOfGenerateClassMethod = new NValInt(index);
        }
        return indexOfGenerateClassMethod;
    }
   
}
