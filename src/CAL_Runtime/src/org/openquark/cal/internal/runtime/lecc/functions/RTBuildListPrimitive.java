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
 * RTRecordToList.java
 * Created: Sept 28, 2006
 * By: Magnus Byne
 */
package org.openquark.cal.internal.runtime.lecc.functions;

import java.util.ArrayList;
import java.util.List;

import org.openquark.cal.internal.runtime.lecc.RTData;
import org.openquark.cal.internal.runtime.lecc.RTExecutionContext;
import org.openquark.cal.internal.runtime.lecc.RTRecordValue;
import org.openquark.cal.internal.runtime.lecc.RTResultFunction;
import org.openquark.cal.internal.runtime.lecc.RTSupercombinator;
import org.openquark.cal.internal.runtime.lecc.RTValue;
import org.openquark.cal.runtime.CALExecutorException;



/**
 * Implements the built-in primitive function: buildList. 
 * This is used to build a homogeneous list
 * using a record dictionary and a tuple of records and lists.
 * 
 * @author Magnus Byne
 */
public final class RTBuildListPrimitive extends RTSupercombinator {

    public static final RTBuildListPrimitive $instance = new RTBuildListPrimitive();

    private RTBuildListPrimitive() {
        // Declared private to limit instantiation.
    }

    public static final RTBuildListPrimitive make(RTExecutionContext $ec) {
        return $instance;
    }

    @Override
    public final int getArity() {
        return 3;
    }

    @Override
    public final RTValue f(final RTResultFunction rootNode, final RTExecutionContext $ec) throws CALExecutorException {

        // Arguments        
        final RTValue record = rootNode.getArgValue();
        RTValue currentRootNode;
        final RTValue index = (currentRootNode = rootNode.prevArg()).getArgValue();
        final RTValue recordDictionary = (currentRootNode = currentRootNode.prevArg()).getArgValue();

        return f3L(recordDictionary, index, record, $ec);
    }

    @Override
    public final RTValue  f3L(RTValue recordDictionary, RTValue index, RTValue record, RTExecutionContext $ec) throws CALExecutorException {

        return f3S(recordDictionary.evaluate($ec), index.evaluate($ec).getIntValue(), record.evaluate($ec), $ec);
    }

    public final RTValue f3S(RTValue recordDictionary, int index, RTValue record, RTExecutionContext $ec) throws CALExecutorException {
        // Since all branches of the function code return a HNF we can simply box the result of the unboxed return method.
        return RTData.CAL_Opaque.make(fUnboxed3S (recordDictionary, index, record, $ec));
    }

    public final List<Object> fUnboxed3S(RTValue recordDictionary, int indexValue, RTValue record, RTExecutionContext $ec) throws CALExecutorException {
        $ec.incrementNMethodCalls();

        /*
         * Loops through all fields in the record dictionary and invokes the
         * dictionary function and adds them to the result list
         */
        RTRecordValue recordDict = (RTRecordValue)recordDictionary;
        RTRecordValue recordValue = (RTRecordValue)record;

        RTValue index = RTData.CAL_Int.make(indexValue);
        
        int numOrdinalFields = recordDict.getNOrdinalFields();
        int numTextualFields = recordDict.getNTextualFields();
        
        int nParams = recordValue.getNOrdinalFields() + recordValue.getNTextualFields();
        //create the list of parameter sources
        ArrayList<RecordParamHelper> paramSources = new ArrayList<RecordParamHelper>(nParams);
        for(int i=0; i<nParams; i++) {
            paramSources.add(RecordParamHelper.create(recordValue.getNthValue(i), $ec));
        }
        
        List<Object> res= new ArrayList<Object>(numOrdinalFields + numTextualFields);
            
        for (int i = 0, nFields = numTextualFields + numOrdinalFields; i < nFields; ++i) {
            
            RTValue fieldDict = recordDict.getNthValue(i);            
            RTValue elem;

            if (indexValue == -1)
                elem = fieldDict;
            else
                elem = fieldDict.apply(index);
            
            //fill arguments using the param sources.
            for(RecordParamHelper param : paramSources) {
                elem = elem.apply(param.getNext($ec));
            }
            
            res.add(elem);
        }
        return res;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getModuleName() {
        return "Cal.Core.Record"; 
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getUnqualifiedName() {
        return "buildListPrimitive";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getQualifiedName() {
        return "Cal.Core.Record.buildListPrimitive";
    }

}
