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
 * NCompareRecord.java
 * Created: Jul 28, 2005 
 * By: Raymond Cypher 
 */

package org.openquark.cal.internal.machine.g.functions;

import java.util.List;

import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.internal.machine.g.Executor;
import org.openquark.cal.internal.machine.g.NPrimitiveFunc;
import org.openquark.cal.internal.machine.g.NRecordValue;
import org.openquark.cal.internal.machine.g.NValInt;
import org.openquark.cal.internal.machine.g.Node;
import org.openquark.cal.internal.module.Cal.Core.CAL_Prelude_internal;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.runtime.CALExecutorException;


/**
 * @author rcypher
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class NCompareRecord extends NPrimitiveFunc {

    public static final QualifiedName name = CAL_Prelude_internal.Functions.compareRecord;
    public static final NCompareRecord instance = new NCompareRecord ();
    
    private static final NValInt Prelude_LT = new NValInt(0);
    private static final NValInt Prelude_EQ = new NValInt(1);
    private static final NValInt Prelude_GT = new NValInt(2);
    
    private NValInt indexOfCompareClassMethod = null;
    
    private NCompareRecord () { /* Constructor made private for creation control. */ }
        
    @Override
    protected int getArity () {
        return 3;
    }
   
    @Override
    protected QualifiedName getName () {
        return name;
    }

    /**
     * Compare two records and return an integer result indicating >, <, or =.   
     */
    @Override
    public Node doEvaluation (Node[] arguments, Executor executor)
            throws CALExecutorException {

        //compareRecord recordDictionary x y.
        //the compiler ensures that the 3 record arguments all have the same fields.
        //we iterate in FieldName order over the field names so that the function is well-defined in the presence of side effects.
        //If f is a field, then recordDictionary.f is the dictionary for use when calling the class method Prelude.compare on
        //the values x.f and y.f.  
        
        // The three arguments in evaluated form will be on top of the stack, in reverse order.
        NRecordValue recordDictionary = (NRecordValue) executor.internalEvaluate(arguments[0]);
        NRecordValue x = (NRecordValue) executor.internalEvaluate(arguments[1]);
        NRecordValue y = (NRecordValue) executor.internalEvaluate(arguments[2]);
        
        // Get the index of the compare class method in the Ord type class. 
        Node classMethodIndex = getIndexOfCompareClassMethod(executor);
        
        List<String> fieldNames = recordDictionary.fieldNames();
        final int nFields = fieldNames.size();
        
        for (int i = 0; i < nFields; ++i) {
            
            String fieldName = fieldNames.get(i);
            Node valueDictionaryThunk = recordDictionary.getValue(fieldName);
            Node xValueThunk = x.getValue(fieldName);
            Node yValueThunk = y.getValue(fieldName);
            
            Node ap = valueDictionaryThunk.apply(classMethodIndex).apply(xValueThunk).apply(yValueThunk);
            
            int compareResult = executor.internalEvaluate(ap).getOrdinalValue ();
            
            switch (compareResult) {
                case 0 :
                {
                    //Prelude.LT
                    return Prelude_LT;
                }
                
                case 1 :
                {
                    //Prelude.EQ                
                    break;
                }
                
                case 2 :
                {
                    //Prelude.GT
                    return Prelude_GT;
                }
                
                default:
                {                               
                    throw new CALExecutorException.InternalException ("Illegal fallthrough to default case in function: " + CAL_Prelude_internal.Functions.compareRecord.getQualifiedName() + ".");            
                }
            }
        }
        
        
        // Prelude.EQ
        return Prelude_EQ;                
    }

    /**
     * Retrieve the current index of the Compare class method.
     * @param executor
     * @return - NValInt holding the index.
     */
    private final NValInt getIndexOfCompareClassMethod (Executor executor) {
        if (indexOfCompareClassMethod == null) {
            final int index = classMethodDictionaryIndex(executor, CAL_Prelude.Functions.compare);                   
            indexOfCompareClassMethod = new NValInt(index);
        }
        return indexOfCompareClassMethod;
    }

}
