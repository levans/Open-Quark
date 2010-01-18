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
 * RTOrdinalValue.java
 * Created: Jul 18, 2005
 * By: Bo Ilic
 */

package org.openquark.cal.internal.runtime.lecc.functions;

import org.openquark.cal.internal.runtime.lecc.LECCMachineConfiguration;
import org.openquark.cal.internal.runtime.lecc.RTData;
import org.openquark.cal.internal.runtime.lecc.RTExecutionContext;
import org.openquark.cal.internal.runtime.lecc.RTResultFunction;
import org.openquark.cal.internal.runtime.lecc.RTSupercombinator;
import org.openquark.cal.internal.runtime.lecc.RTValue;
import org.openquark.cal.runtime.CALExecutorException;


/**
 * Implements the built-in primitive function:
 * primitive private ordinalValue :: a -> Int;
 *
 * The ordinal value is defined as follows:
 * a) for any data constructor defined in an algebraic data declaration, it is the zero-based ordinal
 *    within the declaration. For example, Prelude.LT = 0, Prelude.Eq = 1, and Prelude.GT = 2.
 * b) for any foreign type with java implementation type int, byte, short, or char, the value is the underlying
 *    value, converted to an int. In particular, this is true of the Prelude.Int, Byte, Short and Char types.
 * c) For the built-in Boolean type: Prelude.False = 0, Prelude.True = 1.
 *
 * For values of other types, such as foreign types other than those described in part b), it throws an exception.
 *
 * @author Bo Ilic
 */
public final class RTOrdinalValue extends RTSupercombinator {

    public static final RTOrdinalValue $instance = new RTOrdinalValue();

    private RTOrdinalValue() {
        // Declared private to limit instantiation.
    }

    public static final RTOrdinalValue make(RTExecutionContext $ec) {
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
                RTValue.lastRef(value.evaluate($ec), value = null),
                $ec);
    }

    @Override
    public final RTValue f1L(RTValue value, RTExecutionContext $ec) throws CALExecutorException {

        return f1S(
                RTValue.lastRef(value.evaluate($ec), value = null),
                $ec);
    }

    @Override
    public final RTValue f1S(RTValue value, RTExecutionContext $ec)  {
        // Since all branches of the function code return a HNF we can simply box the result of the unboxed return method.
        return RTData.CAL_Int.make(
                fUnboxed1S(
                        RTValue.lastRef(value, value = null),
                        $ec));
    }

    /**
     * This is the version of the function logic that directly returns an unboxed value.
     * All functions whose return types can be unboxed should have a version of the function
     * logic which returns an unboxed value.
     * @param value
     * @param $ec
     * @return int
     */
    public final int fUnboxed1S(RTValue value, RTExecutionContext $ec) {

        $ec.incrementNMethodCalls();
        if (LECCMachineConfiguration.generateDebugCode() && $ec.isDebugProcessingNeeded(getQualifiedName())) {
            $ec.debugProcessing(getQualifiedName(), new RTValue[]{value});
        }

        return value.getOrdinalValue();
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
        return "ordinalValue";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getQualifiedName() {
        //JUnit tested to equal its binding file value in RuntimeStringConstantsTest.
        return "Cal.Core.Prelude.ordinalValue";
    }
}
