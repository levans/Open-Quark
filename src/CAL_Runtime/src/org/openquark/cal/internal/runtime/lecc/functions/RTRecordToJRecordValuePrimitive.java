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
 * RTRecordToJMapPrimitive.java
 * Created: Jan 9, 2007
 * By: Bo Ilic
 */
package org.openquark.cal.internal.runtime.lecc.functions;
import org.openquark.cal.compiler.FieldName;
import org.openquark.cal.internal.runtime.RecordValueImpl;
import org.openquark.cal.internal.runtime.lecc.LECCMachineConfiguration;
import org.openquark.cal.internal.runtime.lecc.RTData;
import org.openquark.cal.internal.runtime.lecc.RTExecutionContext;
import org.openquark.cal.internal.runtime.lecc.RTRecordValue;
import org.openquark.cal.internal.runtime.lecc.RTResultFunction;
import org.openquark.cal.internal.runtime.lecc.RTSupercombinator;
import org.openquark.cal.internal.runtime.lecc.RTValue;
import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.runtime.RecordValue;



/**
 * Implements the built-in primitive function:
 * recordToJMapPrimitive :: Outputable r => {r} -> JRecordValue
 * defined in the Record module.
 *
 * @author Bo Ilic
 */
public final class RTRecordToJRecordValuePrimitive extends RTSupercombinator {

    public static final RTRecordToJRecordValuePrimitive $instance = new RTRecordToJRecordValuePrimitive();

    private RTRecordToJRecordValuePrimitive() {
        // Declared private to limit instantiation.
    }

    public static final RTRecordToJRecordValuePrimitive make(RTExecutionContext $ec) {
        return $instance;
    }

    @Override
    public final int getArity() {
        return 2;
    }

    @Override
    public final RTValue f(final RTResultFunction rootNode, final RTExecutionContext $ec) throws CALExecutorException {
        // Arguments
        RTValue x = rootNode.getArgValue();
        RTValue recordDictionary = rootNode.prevArg().getArgValue();

        // Release the fields in the root node to open them to garbage collection
        rootNode.clearMembers();

        return f2S (
                RTValue.lastRef(recordDictionary.evaluate($ec), recordDictionary = null),
                RTValue.lastRef(x.evaluate($ec), x = null),
                $ec);
    }

    @Override
    public final RTValue f2L(RTValue recordDictionary, RTValue x, RTExecutionContext $ec) throws CALExecutorException {

        return f2S (
                RTValue.lastRef(recordDictionary.evaluate($ec), recordDictionary = null),
                RTValue.lastRef(x.evaluate($ec), x = null),
                $ec);
    }

    @Override
    public final RTValue f2S(RTValue recordDictionary, RTValue x, RTExecutionContext $ec) throws CALExecutorException {
        // Since all branches of the function code return a HNF we can simply box the result of the unboxed return method.
        return RTData.CAL_Opaque.make(fUnboxed2S (recordDictionary, x, $ec));
    }

    /**
     * This is the version of the function logic that directly returns an unboxed value.
     * All functions whose return types can be unboxed should have a version of the function
     * logic which returns an unboxed value.
     * @param recordDictionary
     * @param x
     * @param $ec
     * @return RecordValue
     * @throws CALExecutorException
     */
    public final RecordValue fUnboxed2S(final RTValue recordDictionary, final RTValue x, final RTExecutionContext $ec) throws CALExecutorException {
        $ec.incrementNMethodCalls();
        if (LECCMachineConfiguration.generateDebugCode() && $ec.isDebugProcessingNeeded(getQualifiedName())) {
            $ec.debugProcessing(getQualifiedName(), new RTValue[]{recordDictionary, x});
        }

        //recordToJMapPrimitive recordDictionary x.
        //the compiler ensures that the 2 record arguments all have the same fields.
        //must iterate in a deterministic order over the field names (as specified by FieldName.CalSourceFormComparator)
        //so that the function is well-defined in the presence of side effects.
        //If f is a field, then recordDictionary.f is the dictionary for use when calling the class method Prelude.output on
        //the value x.f.

        final RTRecordValue recordDict = (RTRecordValue)recordDictionary;
        final RTRecordValue xRecord = (RTRecordValue)x;

        final RecordValue resultMap = RecordValueImpl.make();
        final int nFields = recordDict.getNFields();

        for (int i = 0; i < nFields; ++i) {
            final FieldName fieldName = recordDict.getNthFieldName(i);
            //RTValue fieldDict = recordDictionary.getNthValue(i);
            //RTValue xField = xRecord.getNthValue(i);

            //compute "Prelude.output fieldDict xField"
            //this is just (after inlining Prelude.output d = d"
            //fieldDict xField
            final Object fieldValue = recordDict.getNthValue(i).apply(xRecord.getNthValue(i)).evaluate($ec).getOpaqueValue();
            resultMap.put(fieldName, fieldValue);
        }

        return resultMap;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getModuleName () {
        //JUnit tested to equal its binding file value in RuntimeStringConstantsTest.
        return "Cal.Core.Record";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getUnqualifiedName () {
        //JUnit tested to equal its binding file value in RuntimeStringConstantsTest.
        return "recordToJRecordValuePrimitive";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getQualifiedName() {
        //JUnit tested to equal its binding file value in RuntimeStringConstantsTest.
        return "Cal.Core.Record.recordToJRecordValuePrimitive";
    }
}