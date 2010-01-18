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
 * RTAppendRecordPrimitive.java
 * Created: October 11, 2005
 * By: James Wright
 */

package org.openquark.cal.internal.runtime.lecc.functions;

import org.openquark.cal.internal.runtime.lecc.LECCMachineConfiguration;
import org.openquark.cal.internal.runtime.lecc.RTExecutionContext;
import org.openquark.cal.internal.runtime.lecc.RTRecordValue;
import org.openquark.cal.internal.runtime.lecc.RTResultFunction;
import org.openquark.cal.internal.runtime.lecc.RTSupercombinator;
import org.openquark.cal.internal.runtime.lecc.RTValue;
import org.openquark.cal.runtime.CALExecutorException;


/**
 * Implement the primitive function appendRecordPrimitive defined in the Dynamic module.
 *
 * primitive private appendRecordPrimitive :: {r} -> {s} -> Prelude.CalValue;
 *
 * Return value is an RTValue representing a record extension.
 *
 * @author Jawright
 */
public final class RTAppendRecordPrimitive extends RTSupercombinator {
    public static final RTAppendRecordPrimitive $instance = new RTAppendRecordPrimitive();

    private RTAppendRecordPrimitive() {
        // Empty constructor created for access control.
    }

    public static final RTAppendRecordPrimitive make(RTExecutionContext $ec) {
        return RTAppendRecordPrimitive.$instance;
    }

    @Override
    public int getArity() {
        return 2;
    }

    @Override
    public final RTValue f(final RTResultFunction rootNode, final RTExecutionContext $ec) throws CALExecutorException {
        // Arguments
        RTValue rightRecord = rootNode.getArgValue();
        RTValue leftRecord = rootNode.prevArg().getArgValue();
        rootNode.clearMembers();

        return f2S(
                RTValue.lastRef(leftRecord.evaluate($ec), leftRecord = null),
                RTValue.lastRef(rightRecord.evaluate($ec), rightRecord = null), $ec);
    }

    @Override
    public final RTValue f2L(RTValue leftRecord, RTValue rightRecord, RTExecutionContext $ec) throws CALExecutorException {
        return f2S(
                RTValue.lastRef(leftRecord.evaluate($ec), leftRecord = null),
                RTValue.lastRef(rightRecord.evaluate($ec), rightRecord = null), $ec);
    }

    @Override
    public final RTValue f2S(RTValue leftRecord, RTValue rightRecord, RTExecutionContext $ec) {

        $ec.incrementNMethodCalls();
        if (LECCMachineConfiguration.generateDebugCode() && $ec.isDebugProcessingNeeded(getQualifiedName())) {
            $ec.debugProcessing(getQualifiedName(), new RTValue[]{leftRecord, rightRecord});
        }


        return ((RTRecordValue)leftRecord).appendRecord((RTRecordValue)rightRecord);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getModuleName() {
        //JUnit tested to equal its binding file value in RuntimeStringConstantsTest.
        return "Cal.Core.Dynamic";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getUnqualifiedName() {
        //JUnit tested to equal its binding file value in RuntimeStringConstantsTest.
        return "appendRecordPrimitive";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getQualifiedName() {
        //JUnit tested to equal its binding file value in RuntimeStringConstantsTest.
        return "Cal.Core.Dynamic.appendRecordPrimitive";
    }
}
