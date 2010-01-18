package org.openquark.cal.internal.machine.g.functions;

import java.util.ArrayList;
import java.util.List;

import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.internal.machine.g.Executor;
import org.openquark.cal.internal.machine.g.NPrimitiveFunc;
import org.openquark.cal.internal.machine.g.NRecordValue;
import org.openquark.cal.internal.machine.g.NValInt;
import org.openquark.cal.internal.machine.g.Node;
import org.openquark.cal.internal.module.Cal.Core.CAL_Record_internal;
import org.openquark.cal.runtime.CALExecutorException;

public class NBuildRecord extends NPrimitiveFunc {

    public static final QualifiedName name = CAL_Record_internal.Functions.buildRecordPrimitive;
    public static final NBuildRecord instance = new NBuildRecord ();
       
   
    private NBuildRecord () {/* Constructor made private to control creation. */ }
    
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
        
        List<String> fieldNames = recordDict.fieldNames();

        NRecordValue result=new NRecordValue(nFields);
        
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
            String fieldName = fieldNames.get(i);
            result= result.insertRecordField(fieldName, executor.internalEvaluate(elem));
        }
        
        return result;
    }
   
}