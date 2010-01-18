package org.openquark.cal.internal.runtime.lecc.functions;

import java.util.NoSuchElementException;

import org.openquark.cal.internal.runtime.lecc.RTCons;
import org.openquark.cal.internal.runtime.lecc.RTExecutionContext;
import org.openquark.cal.internal.runtime.lecc.RTRecordValue;
import org.openquark.cal.internal.runtime.lecc.RTValue;
import org.openquark.cal.runtime.CALExecutorException;

/**
 * This class is used by the BuildList and BuildRecord primitive functions
 * to extract values from lists and records.
 * 
 * @author mbyne
 */
public abstract class RecordParamHelper {

    private static class TupleParam extends RecordParamHelper {
        int current;
        RTRecordValue tuple;
        
        TupleParam(RTValue tuple) {
            current = 0;
            this.tuple = (RTRecordValue) tuple;
        }
    
        RTValue getNext(RTExecutionContext $ec) throws CALExecutorException {
            return tuple.getNthValue(current++);
        }
    }
    
    /**
     * This class is used for extracting parameter values from a list
     * @author mbyne
     */
    private static class ListParam extends RecordParamHelper {
        private RTValue calListValue;

        ListParam(RTValue list) {
            calListValue = list;
        }

        RTValue getNext(RTExecutionContext $ec) throws CALExecutorException {
            RTValue listItem; // this is the item used to generate the record
            // value
            calListValue = calListValue.evaluate($ec);
            switch (calListValue.getOrdinalValue()) {
            case 0: {
                // Prelude.Nil
                throw new NoSuchElementException();
            }

            case 1: {
                // Prelude.Cons
                RTCons listCons = (RTCons) calListValue;
                listItem = listCons.getFieldByIndex(1, 0, null);
                calListValue = listCons.getFieldByIndex(1, 1, null);

                listItem.evaluate($ec);
                break;
            }

            default: {
                throw new IndexOutOfBoundsException();
            }

            }
            return listItem;
        }
    }

    abstract RTValue getNext(RTExecutionContext $ec) throws CALExecutorException;

    static RecordParamHelper create(RTValue v, RTExecutionContext $ec) throws CALExecutorException {
        v = v.evaluate($ec);
        if (v instanceof RTRecordValue) {
            return new TupleParam(v);
        } else {
            return new ListParam(v);
        }

    }
}
