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
 * RTRecordFromJListPrimitive.java
 * Created: Jun 25, 2004
 * By: Bo Ilic
 */
package org.openquark.cal.internal.runtime.lecc.functions;

import java.util.Iterator;
import java.util.List;

import org.openquark.cal.internal.runtime.lecc.LECCMachineConfiguration;
import org.openquark.cal.internal.runtime.lecc.RTData;
import org.openquark.cal.internal.runtime.lecc.RTExecutionContext;
import org.openquark.cal.internal.runtime.lecc.RTFullApp;
import org.openquark.cal.internal.runtime.lecc.RTRecordValue;
import org.openquark.cal.internal.runtime.lecc.RTResultFunction;
import org.openquark.cal.internal.runtime.lecc.RTSupercombinator;
import org.openquark.cal.internal.runtime.lecc.RTValue;
import org.openquark.cal.internal.runtime.lecc.RTData.CAL_Opaque;
import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.runtime.CalValue;

/**
 * Implements the built-in primitive function:
 * recordFromJListPrimitive :: Inputable r => JList -> {r}
 *
 * @author Bo Ilic
 */
public final class RTRecordFromJListPrimitive extends RTSupercombinator {

    public static final RTRecordFromJListPrimitive $instance = new RTRecordFromJListPrimitive();

    private RTRecordFromJListPrimitive() {
        // Declared as private to limit instantiation.
    }

    public static final RTRecordFromJListPrimitive make(RTExecutionContext $ec) {
        return $instance;
    }

    @Override
    public final int getArity() {
        return 2;
    }

    @Override
    public final RTValue f(final RTResultFunction rootNode, final RTExecutionContext $ec) throws CALExecutorException {
        // Arguments
        RTValue javaList = rootNode.getArgValue();
        RTValue recordDictionary = rootNode.prevArg().getArgValue();

        // Release the fields in the root node to open them to garbage collection
        rootNode.clearMembers();

        return f2S (
                RTValue.lastRef(recordDictionary.evaluate($ec), recordDictionary = null),
                RTRecordFromJListPrimitive.lastRef((List<?>)javaList.evaluate($ec).getOpaqueValue(), javaList = null),
                $ec);
    }

    @Override
    public final RTValue f2L(RTValue recordDictionary, RTValue javaList, RTExecutionContext $ec) throws CALExecutorException {

        return f2S (
                RTValue.lastRef(recordDictionary.evaluate($ec), recordDictionary = null),
                RTRecordFromJListPrimitive.lastRef((List<?>)javaList.evaluate($ec).getOpaqueValue(), javaList = null),
                $ec);
    }

    public final RTValue f2S(final RTValue recordDictionary, final List<?> inputList, final RTExecutionContext $ec) {
        $ec.incrementNMethodCalls();
        if (LECCMachineConfiguration.generateDebugCode() && $ec.isDebugProcessingNeeded(getQualifiedName())) {
            $ec.debugProcessing(getQualifiedName(), new RTValue[]{recordDictionary, CAL_Opaque.make(inputList)});
        }

        //recordFromJListPrimitive recordDict inputList.
        //we iterate in FieldName order over the field names so that the function is well-defined in the presence of side effects.
        //If f is the nth field in the recordDictionary, then recordDictionary.f is the dictionary for use when calling the
        //class method Prelude.input on the nth element of the input list.

        final RTRecordValue recordDict = (RTRecordValue)recordDictionary;

        final int nOrdinalFields = recordDict.getNOrdinalFields();
        final int nTextualFields = recordDict.getNTextualFields();

        final int nFields = nOrdinalFields + nTextualFields;
        //check that the number of fields in the inputList is the same as the number of fields in the record.
        //without this check it is possible that inputList could have more elements than the size of the record and still succeed.
        //This would still "work" but this check is useful to alert clients to potential bugs in their code.
        if (nFields != inputList.size()) {
            throw new IllegalArgumentException("A Java list of size " + inputList.size() + " cannot be input to a record with " + nFields + " fields.");
        }

        if (nOrdinalFields > 0) {

            //we use an iterator since inputList may be a non-random access list such as a java.util.LinkedList.
            Iterator<?> inputListIterator = inputList.iterator();

            RTValue[] ordinalValues = new RTValue[nOrdinalFields];
            for (int i = 0; i < nOrdinalFields; ++i) {

                RTValue fieldDict = recordDict.getNthOrdinalValue(i);
                RTValue listElement = RTData.CAL_Opaque.make(inputListIterator.next());

                //compute "Prelude.input fieldDict listElement"
                //this is just (after inlining Prelude.input d = d)
                //fieldDict listElement

                ordinalValues[i] = fieldDict.apply(listElement);
            }

            if (nTextualFields > 0) {

                RTValue [] textualValues = new RTValue[nTextualFields];
                for (int i = 0; i < nTextualFields; ++i) {

                    RTValue fieldDict = recordDict.getNthTextualValue(i);
                    RTValue listElement = RTData.CAL_Opaque.make(inputListIterator.next());

                    textualValues[i] = fieldDict.apply(listElement);
                }

                return recordDict.makeFromValues(ordinalValues, textualValues);
            }

            return recordDict.makeFromValues(ordinalValues, null);
        }

        if (nTextualFields > 0) {

            //we use an iterator since inputList may be a non-random access list such as a java.util.LinkedList.
            Iterator<?> inputListIterator = inputList.iterator();

            RTValue [] textualValues = new RTValue[nTextualFields];
            for (int i = 0; i < nTextualFields; ++i) {

                RTValue fieldDict = recordDict.getNthTextualValue(i);
                RTValue listElement = RTData.CAL_Opaque.make(inputListIterator.next());

                textualValues[i] = fieldDict.apply(listElement);
            }

            return recordDict.makeFromValues(null, textualValues);
        }

        //empty record
        return RTRecordValue.EMPTY_RECORD;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getModuleName () {
        //JUnit tested to equal its binding file value in RuntimeStringConstantsTest.
        return "Cal.Core.Prelude";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getUnqualifiedName () {
        //JUnit tested to equal its binding file value in RuntimeStringConstantsTest.
        return "recordFromJListPrimitive";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getQualifiedName() {
        //JUnit tested to equal its binding file value in RuntimeStringConstantsTest.
        return "Cal.Core.Prelude.recordFromJListPrimitive";
    }

    /**
     * lastRef is used as a means of nulling out local fields/variables
     * at the same time they are being passed to as arguments in a method call.
     * For example:
     *      myclass.myMethod(RTValue.lastRef(arg, arg = null));
     * This is, of course, only used if there are no further references to the
     * local in the code containing the method call.
     *
     * In essence this removes the local reference after it has been passed to
     * myMethod but before myMethod is evaluated.
     *
     * The purpose behind these contortions is to make the object passed as a
     * method argument available for garbage collection.  Normally the local
     * reference would prevent garbage collection until the call to myMethod
     * returned and the local reference could be nulled out.
     *
     *  If the argument is the root node of a dynamically expanded data structure
     *  (ex. a List) the reference to the root node will keep the entire structure
     *  in memory, even if the code in myMethod releases its reference to the root
     *  as it traverses the data structure.  By nulling out the local using lastRef
     *  the garbage collector can collect the traversed portion of the data
     *  structure, potentially significantly reducing the memory used.
     *
     * @param i
     * @param v
     * @return The value of the first argument.
     */
    private static final List<?> lastRef (List<?> i, RTValue v) {
        return i;
    }

    /**
     * A fully saturated application of recordFromJListPrimitive.
     * Note:  This class doesn't need to implement getArity() or getArgCount()
     * as a fully saturated application can be treated like a zero arity function
     * and the base implementation of both these methods returns zero.
     * @author rcypher
     */
   public static final class RTAppS extends RTFullApp {
        private final RTRecordFromJListPrimitive function;
        private RTValue recordDictionary;
        private List<?> inputList;

        public RTAppS(RTRecordFromJListPrimitive function, RTValue recordDictionary, List<?> inputList) {
            assert (function != null && recordDictionary != null && inputList != null) :
                "Null argument in constructor for RTError.RTAppS.";

            this.function = function;
            this.recordDictionary = recordDictionary;
            this.inputList = inputList;
        }

        @Override
        protected final RTValue reduce(RTExecutionContext $ec) {
            if (result == null) {
                setResult(function.f2S(recordDictionary, inputList, $ec));
                clearMembers();
            }
            return result;
        }

        @Override
        public final void clearMembers() {
            //we don't need to clear this.function, since it points to a singleton held by a static member field
            recordDictionary = null;
            inputList = null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public final int debug_getNChildren() {
            if (result != null) {
                return super.debug_getNChildren();
            }
            return 2;
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public final CalValue debug_getChild(int childN) {
            if (result != null) {
                return super.debug_getChild(childN);
            }

            switch (childN) {
            case 0:
                return recordDictionary;
            case 1:
                return CAL_Opaque.make(inputList);
            default:
                throw new IndexOutOfBoundsException();
            }
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public final String debug_getNodeStartText() {
            if (result != null) {
                return super.debug_getNodeStartText();
            }

            return "(" + function.getQualifiedName();
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public final String debug_getNodeEndText() {
            if (result != null) {
                return super.debug_getNodeEndText();
            }

            return ")";
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public final String debug_getChildPrefixText(int childN) {
            if (result != null) {
                return super.debug_getChildPrefixText(childN);
            }

            if (childN >= 0 && childN < 2) {
                return " ";
            }

            throw new IndexOutOfBoundsException();
        }
    }


}
