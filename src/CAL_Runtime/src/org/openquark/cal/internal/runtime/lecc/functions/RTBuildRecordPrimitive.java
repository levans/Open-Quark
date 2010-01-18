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
 * RTListToRecord.java
 * Created: Oct 2, 2006
 * By: Magnus Byne
 */

package org.openquark.cal.internal.runtime.lecc.functions;

import java.util.ArrayList;

import org.openquark.cal.internal.runtime.lecc.RTData;
import org.openquark.cal.internal.runtime.lecc.RTExecutionContext;
import org.openquark.cal.internal.runtime.lecc.RTRecordValue;
import org.openquark.cal.internal.runtime.lecc.RTResultFunction;
import org.openquark.cal.internal.runtime.lecc.RTSupercombinator;
import org.openquark.cal.internal.runtime.lecc.RTValue;
import org.openquark.cal.runtime.CALExecutorException;

/**
 * Implements the built-in primitive function: buildRecord. 
 * This is used to build a record
 * using a record dictionary and a tuple of records and lists.
 * 
 * @author Magnus Byne
 */
public final class RTBuildRecordPrimitive extends RTSupercombinator {

    public static final RTBuildRecordPrimitive $instance = new RTBuildRecordPrimitive();

    private RTBuildRecordPrimitive() {
        // Declared private to limit instantiation.
    }

    public static final RTBuildRecordPrimitive make(RTExecutionContext $ec) {
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

        return f3S(recordDictionary.evaluate($ec), index.evaluate($ec), record.evaluate($ec), $ec);
    }

    public final RTValue f3S(RTValue recordDictionary, int index, RTValue args, RTExecutionContext $ec) throws CALExecutorException {
        $ec.incrementNMethodCalls();

        /*
         * Loops through all fields in the record dictionary and invokes the
         * class method to construct each record field
         */

        RTRecordValue recordDict = (RTRecordValue)recordDictionary;
        RTRecordValue recordValue = (RTRecordValue) args;

        RTValue indexValue = RTData.CAL_Int.make(index);
        
        int numOrdinalFields = recordDict.getNOrdinalFields();
        int numTextualFields = recordDict.getNTextualFields();
        
        RTValue ordinalValues[] = (numOrdinalFields > 0) ? new RTValue[numOrdinalFields] : null;
        RTValue textualValues[] = (numTextualFields > 0) ? new RTValue[numTextualFields] : null;

        int nParams = recordValue.getNOrdinalFields() + recordValue.getNTextualFields();
        //create the list of parameter sources
        ArrayList<RecordParamHelper> paramSources = new ArrayList<RecordParamHelper>(nParams);
        for(int i=0; i<nParams; i++) {
            paramSources.add(RecordParamHelper.create(recordValue.getNthValue(i), $ec));
        }
        
        for (int i = 0, nFields = numTextualFields + numOrdinalFields; i < nFields; ++i) {
            
            RTValue fieldDict = recordDict.getNthValue(i);            
            RTValue elem;
            
            RTValue f;
            if (index == -1)
                f = fieldDict;
            else
                f = fieldDict.apply(indexValue);
            
            //fill f's arguments using the param sources.
            for(RecordParamHelper param : paramSources) {
                f = f.apply(param.getNext($ec));
            }
            
            elem = f.evaluate($ec);
            
            if (i < recordDict.getNOrdinalFields()) {
                ordinalValues[i] = elem;
            } else {
                textualValues[i - numOrdinalFields] = elem;
            }
        }

        return recordDict.makeFromValues(ordinalValues, textualValues);
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
        return "buildRecordPrimitive";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getQualifiedName() {
        return "Cal.Core.Record.buildRecordPrimitive";
    }

}
