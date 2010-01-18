package org.openquark.cal.internal.machine.g.functions;

import java.util.NoSuchElementException;

import org.openquark.cal.internal.machine.g.Executor;
import org.openquark.cal.internal.machine.g.NConstr2;
import org.openquark.cal.internal.machine.g.NRecordValue;
import org.openquark.cal.internal.machine.g.Node;
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
        NRecordValue tuple;
        
        TupleParam(Node tuple) {
            current = 0;
            this.tuple = (NRecordValue) tuple;
        }
    
        Node getNext(Executor $ec) throws CALExecutorException {
            return tuple.fieldValues().get(current++);
        }
    }
    
    /**
     * This class is used for extracting parameter values from a list
     * @author mbyne
     */
    private static class ListParam extends RecordParamHelper {
        private Node calListValue;

        ListParam(Node list) {
            calListValue = list;
        }

        Node getNext(Executor $ec) throws CALExecutorException {
            Node listItem; // this is the item used to generate the record
            // value
            calListValue = $ec.internalEvaluate(calListValue);
            switch (calListValue.getOrdinalValue()) {
            case 0: {
                // Prelude.Nil
                throw new NoSuchElementException();
            }

            case 1: {
                // Prelude.Cons
                NConstr2 listCons = (NConstr2) calListValue;
                listItem = $ec.internalEvaluate(listCons.getN0());
                calListValue = listCons.getN1();

                break;
            }

            default: {
                throw new IndexOutOfBoundsException();
            }

            }
            return listItem;
        }
    }

    abstract Node getNext(Executor $ec) throws CALExecutorException;

    static RecordParamHelper create(Node v, Executor $ec) throws CALExecutorException {
        v = $ec.internalEvaluate(v);
        if (v instanceof NRecordValue) {
            return new TupleParam(v);
        } else {
            return new ListParam(v);
        }

    }
}

