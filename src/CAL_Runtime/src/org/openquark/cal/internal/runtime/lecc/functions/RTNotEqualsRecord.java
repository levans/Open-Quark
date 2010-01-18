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
 * RTNotEqualsRecord.java
 * Created: Jun 25, 2004
 * By: Bo Ilic
 */
package org.openquark.cal.internal.runtime.lecc.functions;

import org.openquark.cal.internal.runtime.lecc.LECCMachineConfiguration;
import org.openquark.cal.internal.runtime.lecc.RTData;
import org.openquark.cal.internal.runtime.lecc.RTExecutionContext;
import org.openquark.cal.internal.runtime.lecc.RTOApp3;
import org.openquark.cal.internal.runtime.lecc.RTRecordValue;
import org.openquark.cal.internal.runtime.lecc.RTResultFunction;
import org.openquark.cal.internal.runtime.lecc.RTSupercombinator;
import org.openquark.cal.internal.runtime.lecc.RTValue;
import org.openquark.cal.runtime.CALExecutorException;


/**
 * Implements the built-in primitive function:
 * notEqualsRecord :: Eq r => {r} -> {r} -> Boolean
 *
 * @author Bo Ilic
 */
public final class RTNotEqualsRecord extends RTSupercombinator {

    /**
     * The index of the Prelude.notEquals class method This is currently
     * hard-coded. Changes to the type class would necessitate updating this
     * value.
     */
    private static final RTData.CAL_Int indexOfNotEqualsClassMethod = RTData.CAL_Int.make(1);

    public static final RTNotEqualsRecord $instance = new RTNotEqualsRecord();

    private RTNotEqualsRecord() {
        // Declared private to limit instantiation.
    }

    public static final RTNotEqualsRecord make(RTExecutionContext $ec) {
        return $instance;
    }

    @Override
    public final int getArity() {
        return 3;
    }

    @Override
    public final RTValue f(final RTResultFunction rootNode, final RTExecutionContext $ec) throws CALExecutorException {
        // Arguments
        RTValue y = rootNode.getArgValue();
        RTValue currentRootNode;
        RTValue x = (currentRootNode = rootNode.prevArg()).getArgValue();
        RTValue recordDictionary = currentRootNode.prevArg().getArgValue();

        // Release the fields in the root node to open them to garbage collection
        rootNode.clearMembers();

        return f3S (
                RTValue.lastRef(recordDictionary.evaluate($ec), recordDictionary = null),
                RTValue.lastRef(x.evaluate($ec), x = null),
                RTValue.lastRef(y.evaluate($ec), y = null),
                $ec);
    }

    @Override
    public final RTValue f3L(RTValue recordDictionary, RTValue x, RTValue y, RTExecutionContext $ec) throws CALExecutorException {

        return f3S (
                RTValue.lastRef(recordDictionary.evaluate($ec), recordDictionary = null),
                RTValue.lastRef(x.evaluate($ec), x = null),
                RTValue.lastRef(y.evaluate($ec), y = null),
                $ec);
    }

    @Override
    public final RTValue f3S(RTValue recordDictionary, RTValue x, RTValue y, RTExecutionContext $ec) throws CALExecutorException {
        $ec.incrementNMethodCalls();
        if (LECCMachineConfiguration.generateDebugCode() && $ec.isDebugProcessingNeeded(getQualifiedName())) {
            $ec.debugProcessing(getQualifiedName(), new RTValue[]{recordDictionary, x, y});
        }

        //notEqualsRecord recordDictionary x y.
        //the compiler ensures that the 3 record arguments all have the same fields.
        //must iterate in a deterministic order over the field names (as specified by FieldName.CalSourceFormComparator)
        //so that the function is well-defined in the presence of side effects.
        //If f is a field, then recordDictionary.f is the dictionary for use when calling the class method Prelude.notEquals on
        //the values x.f and y.f.

        final RTRecordValue recordDict = (RTRecordValue)recordDictionary;
        final RTRecordValue xRecord = (RTRecordValue)x;
        final RTRecordValue yRecord = (RTRecordValue)y;

        for (int i = 0, nFields = recordDict.getNFields(); i < nFields; ++i) {

            final RTValue fieldDict = recordDict.getNthValue(i);
            final RTValue xField = xRecord.getNthValue(i);
            final RTValue yField = yRecord.getNthValue(i);

            //compute "Prelude.notEquals fieldDict xField yField"
            //this is just (after inlining Prelude.notEquals d = d indexOfNotEqualsClassMethod"
            //fieldDict indexOfNotEqualsClassMethod xField yField

            //the commented out version also works, but is less optimal
            //if (fieldDict.apply(indexOfNotEqualsClassMethod, xField, yField).evaluate($ec).isLogicalTrue()) {

            if ((new RTOApp3(fieldDict, indexOfNotEqualsClassMethod, xField, yField).evaluate($ec).isLogicalTrue())) {
                return RTData.CAL_Boolean.TRUE;
            }
        }

        return RTData.CAL_Boolean.FALSE;
    }

    /**
     * This is the version of the function logic that directly returns an unboxed value.
     * All functions whose return types can be unboxed should have a version of the function
     * logic which returns an unboxed value.
     * @param recordDictionary
     * @param x
     * @param y
     * @param $ec
     * @return boolean
     * @throws CALExecutorException
     */
    public final boolean fUnboxed3Sf3S(RTValue recordDictionary, RTValue x, RTValue y, RTExecutionContext $ec) throws CALExecutorException {
        $ec.incrementNMethodCalls();
        if (LECCMachineConfiguration.generateDebugCode() && $ec.isDebugProcessingNeeded(getQualifiedName())) {
            $ec.debugProcessing(getQualifiedName(), new RTValue[]{recordDictionary, x, y});
        }

        //notEqualsRecord recordDictionary x y.
        //the compiler ensures that the 3 record arguments all have the same fields.
        //must iterate in a deterministic order over the field names (as specified by FieldName.CalSourceFormComparator)
        //so that the function is well-defined in the presence of side effects.
        //If f is a field, then recordDictionary.f is the dictionary for use when calling the class method Prelude.notEquals on
        //the values x.f and y.f.

        final RTRecordValue recordDict = (RTRecordValue)recordDictionary;
        final RTRecordValue xRecord = (RTRecordValue)x;
        final RTRecordValue yRecord = (RTRecordValue)y;

        for (int i = 0, nFields = recordDict.getNFields(); i < nFields; ++i) {

            final RTValue fieldDict = recordDict.getNthValue(i);
            final RTValue xField = xRecord.getNthValue(i);
            final RTValue yField = yRecord.getNthValue(i);

            //compute "Prelude.notEquals fieldDict xField yField"
            //this is just (after inlining Prelude.notEquals d = d indexOfNotEqualsClassMethod"
            //fieldDict indexOfNotEqualsClassMethod xField yField

            //the commented out version also works, but is less optimal
            //if (fieldDict.apply(indexOfNotEqualsClassMethod, xField, yField).evaluate($ec).isLogicalTrue()) {

            if ((new RTOApp3(fieldDict, indexOfNotEqualsClassMethod, xField, yField).evaluate($ec).isLogicalTrue())) {
                return true;
            }
        }

        return false;
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
        return "notEqualsRecord";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getQualifiedName() {
        //JUnit tested to equal its binding file value in RuntimeStringConstantsTest.
        return "Cal.Core.Prelude.notEqualsRecord";
    }
}
