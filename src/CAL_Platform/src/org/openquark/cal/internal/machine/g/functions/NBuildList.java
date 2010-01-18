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

package org.openquark.cal.internal.machine.g.functions;

import java.util.ArrayList;
import java.util.List;

import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.internal.machine.g.Executor;
import org.openquark.cal.internal.machine.g.NPrimitiveFunc;
import org.openquark.cal.internal.machine.g.NRecordValue;
import org.openquark.cal.internal.machine.g.NValInt;
import org.openquark.cal.internal.machine.g.NValObject;
import org.openquark.cal.internal.machine.g.Node;
import org.openquark.cal.internal.module.Cal.Core.CAL_Record_internal;
import org.openquark.cal.runtime.CALExecutorException;

/**
 * Implements the built-in primitive function for converting a Record to a List.
 * for the G machine.
 * 
 * @author Magnus Byne
 */
public class NBuildList extends NPrimitiveFunc {

    public static final QualifiedName name = CAL_Record_internal.Functions.buildListPrimitive;
    public static final NBuildList instance = new NBuildList ();
       
   
    private NBuildList () {/* Constructor made private to control creation. */ }
    
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
        NRecordValue recordDict = (NRecordValue) executor.internalEvaluate(arguments[0]);
        NValInt index = (NValInt) executor.internalEvaluate(arguments[1]);
        NRecordValue recordValue = (NRecordValue) executor.internalEvaluate(arguments[2]);
        
        int nFields = recordDict.getNFields();
        
        List<Node> result = new ArrayList<Node>(nFields);
        
        int nParams = recordValue.getNFields();
        ArrayList<RecordParamHelper> paramSources = new ArrayList<RecordParamHelper>(nParams);
        for(int i=0; i<nParams; i++) {
            paramSources.add(RecordParamHelper.create(recordValue.getNthValue(i), executor));
        }
 
        for (int i = 0; i < nFields; ++i) {
            
            Node fieldDict = recordDict.getNthValue(i);            
            Node elem;

            if (index.getIntValue() == -1)
                elem = fieldDict;
            else
                elem = fieldDict.apply(index);
            
            //fill f's arguments using the param sources.
            for(RecordParamHelper param : paramSources) {
                elem = elem.apply(param.getNext(executor));
            }
            
            result.add(elem);
        }
        
        return new NValObject(result);
    }
   
}
