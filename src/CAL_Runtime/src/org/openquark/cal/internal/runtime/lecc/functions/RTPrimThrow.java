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
 * RTThrow.java
 * Created: Aug 29, 2006
 * By: Bo Ilic
 */

package org.openquark.cal.internal.runtime.lecc.functions;

import org.openquark.cal.internal.runtime.lecc.LECCMachineConfiguration;
import org.openquark.cal.internal.runtime.lecc.RTExecutionContext;
import org.openquark.cal.internal.runtime.lecc.RTResultFunction;
import org.openquark.cal.internal.runtime.lecc.RTSupercombinator;
import org.openquark.cal.internal.runtime.lecc.RTValue;
import org.openquark.cal.runtime.CALExecutorException;


/**
 * Implements the primitive function defined in the Exception module:
 * primThrow :: JThrowable -> a;
 *
 * @author Bo Ilic
 */
public final class RTPrimThrow extends RTSupercombinator {

    public static final RTPrimThrow $instance = new RTPrimThrow();

    private RTPrimThrow() {
        // Declared private to limit instantiation.
    }

    public static final RTPrimThrow make(RTExecutionContext $ec) {
        return $instance;
    }

    @Override
    public final int getArity() {
        return 1;
    }

    @Override
    public final RTValue f(final RTResultFunction rootNode, final RTExecutionContext $ec) throws CALExecutorException {
        // Arguments
        RTValue value = rootNode.getArgValue();

        // Release the fields in the root node to open them to garbage collection
        rootNode.clearMembers();

        return f1S(
                RTValue.lastRef(value, value = null),
                $ec);
    }

    @Override
    public final RTValue f1L(RTValue value, RTExecutionContext $ec) throws CALExecutorException {

        return f1S(
                RTValue.lastRef(value, value = null),
                $ec);
    }

    @Override
    public final RTValue f1S(RTValue value, RTExecutionContext $ec) throws CALExecutorException {

        $ec.incrementNMethodCalls();
        if (LECCMachineConfiguration.generateDebugCode() && $ec.isDebugProcessingNeeded(getQualifiedName())) {
            $ec.debugProcessing(getQualifiedName(), new RTValue[]{value});
        }

        value = value.evaluate($ec);

        throw new CALExecutorException.ExternalException.PrimThrowFunctionException($instance.getQualifiedName() + " called.", (Throwable)value.getOpaqueValue());
    }

    /** {@inheritDoc} */
    @Override
    public String getModuleName() {
        //JUnit tested to equal its binding file value in RuntimeStringConstantsTest.
        return "Cal.Core.Exception";
    }

    /** {@inheritDoc} */
    @Override
    public String getUnqualifiedName() {
        //JUnit tested to equal its binding file value in RuntimeStringConstantsTest.
        return "primThrow";
    }

    /** {@inheritDoc} */
    @Override
    public String getQualifiedName() {
        //JUnit tested to equal its binding file value in RuntimeStringConstantsTest.
        return "Cal.Core.Exception.primThrow";
    }
}
