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
 * NShowRecord.java
 * Created: Jul 28, 2005 
 * By: Raymond Cypher 
 */

package org.openquark.cal.internal.machine.g.functions;

import java.util.List;

import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.internal.machine.g.Executor;
import org.openquark.cal.internal.machine.g.NAp;
import org.openquark.cal.internal.machine.g.NPrimitiveFunc;
import org.openquark.cal.internal.machine.g.NRecordValue;
import org.openquark.cal.internal.machine.g.NValObject;
import org.openquark.cal.internal.machine.g.Node;
import org.openquark.cal.internal.module.Cal.Core.CAL_Debug_internal;
import org.openquark.cal.module.Cal.Core.CAL_Debug;
import org.openquark.cal.runtime.CALExecutorException;


/**
 * @author rcypher
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class NShowRecord extends NPrimitiveFunc {

    public static final QualifiedName name = CAL_Debug_internal.Functions.showRecord;
    public static final NShowRecord instance = new NShowRecord ();
    
    private boolean showRecordImplementationChecked = false;
    
    private NShowRecord () {/* constructor made private for singleton */ }
  
    @Override
    protected int getArity () {
        return 2;
    }
   
    @Override
    protected QualifiedName getName () {
        return name;
    }

    /**
     * Convert a record to the corresponding string representation and return the string.
     */
    @Override
    public Node doEvaluation (Node[] arguments, Executor executor)
            throws CALExecutorException {

        //showRecord recordDictionary x.
        //the compiler ensures that the 2 record arguments all have the same fields.
        //must iterate in a deterministic order over the field names (as specified by FieldName.CalSourceFormComparator)
        //so that the function is well-defined in the presence of side effects.
        //If f is a field, then recordDictionary.f is the dictionary for use when calling the class method Debug.show on
        //the value x.f.  
        
        checkShowRecordImplementation (executor);
        
        // Evaluat the two arguments.
        NRecordValue recordDictionary = (NRecordValue) executor.internalEvaluate(arguments[0]);
        NRecordValue x = (NRecordValue) executor.internalEvaluate(arguments[1]);
        
        List<String> fieldNames = recordDictionary.fieldNames();        
        int nFields = fieldNames.size();
        
        //tuple records (with 2 or more fields) are displayed using the parentheses notation where field-names are omitted.
        boolean isTuple = fieldNames.size() >= 2;
        for (int i = 1; isTuple && i <= fieldNames.size(); ++i) {
            String fieldName = fieldNames.get(i-1);
            if (!fieldName.equals("#"+i)) {
                isTuple = false;
            }
        }
        
        StringBuilder showResult = new StringBuilder();
        if (isTuple) {
            showResult.append ("(");
            
            for (int i = 0; i < nFields; ++i) {
                if (i > 0) {
                    showResult.append (", ");
                }
                
                //compute "Debug.show valueDictionaryThunk xValueThunk"
                //this is just (after inlining Debug.show d = d)
                //valueDictionaryThunk xValueThunk
                
                String fieldName = fieldNames.get(i);
                Node valueDictionaryThunk = recordDictionary.getValue(fieldName);
                Node xValueThunk = x.getValue(fieldName);
                Node ap = valueDictionaryThunk.apply (xValueThunk);
                
                String apResult = (String)(executor.internalEvaluate(ap).getValue());
                
                showResult.append(apResult);
            }
            
            showResult.append(")");
            
        } else {
            showResult.append ("{");
            
            for (int i = 0; i < nFields; ++i) {
                if (i > 0) {
                    showResult.append (", ");
                }
                
                String fieldName = fieldNames.get(i);
                showResult.append(fieldName).append(" = ");
                
                //compute "Debug.show valueDictionaryThunk xValueThunk"
                //this is just (after inlining Debug.show d = d)
                //valueDictionaryThunk xValueThunk
                Node valueDictionaryThunk = recordDictionary.getValue(fieldName);
                Node xValueThunk = x.getValue(fieldName);
                Node ap = new NAp(valueDictionaryThunk, xValueThunk);
                
                String apResult = (String)(executor.internalEvaluate(ap).getValue());
                
                showResult.append(apResult);
            }
            
            showResult.append("}");
        }
        
        return (new NValObject(showResult.toString()));

    }

    private void checkShowRecordImplementation (Executor executor) {
        if(!showRecordImplementationChecked) {
            if (!isSingleMethodRootClass(executor, CAL_Debug.TypeClasses.Show)) {            
                throw new IllegalStateException("The implementation of " + CAL_Debug_internal.Functions.showRecord.getQualifiedName() + " assumes that the class " + CAL_Debug.TypeClasses.Show.getQualifiedName() + " has no superclasses and only 1 class method.");
            }
            showRecordImplementationChecked = true;
        }
    }
    
 
}
