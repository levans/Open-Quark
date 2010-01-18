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
 * RTEqualsRecord.java
 * Created: Sept 28, 2006
 * By: Magnus Byne
 */
package org.openquark.cal.internal.runtime.lecc.functions;

import org.openquark.cal.internal.runtime.lecc.RTData;
import org.openquark.cal.internal.runtime.lecc.RTExecutionContext;
import org.openquark.cal.internal.runtime.lecc.RTRecordValue;
import org.openquark.cal.internal.runtime.lecc.RTResultFunction;
import org.openquark.cal.internal.runtime.lecc.RTSupercombinator;
import org.openquark.cal.internal.runtime.lecc.RTValue;
import org.openquark.cal.runtime.CALExecutorException;



/**
 * Implements the built-in primitive function:
 * coarbitraryRecordPrimitive :: Arbitrary r => GenParams -> (GenParams -> GenParams) -> {r}
 *
 * @author Magnus Byne
 */
public final class RTCoArbitraryRecordPrimitive extends RTSupercombinator {

    /**
     * the index of the Arbitrary.coarbitrary class method. This is currently
     * hard-coded. Changes to the type class would necessitate updating this
     * value.
     */
    private static final RTData.CAL_Int indexOfCoArbitraryClassMethod = RTData.CAL_Int.make(1);

    public static final RTCoArbitraryRecordPrimitive $instance = new RTCoArbitraryRecordPrimitive();

    private RTCoArbitraryRecordPrimitive() {
        // Declared private to limit instantiation.
    }

    public static final RTCoArbitraryRecordPrimitive make(RTExecutionContext $ec) {
        return $instance;
    }

    @Override
    public final int getArity() {
        return 3;
    }

    @Override
    public final RTValue f(final RTResultFunction rootNode, final RTExecutionContext $ec) throws CALExecutorException {
        // Arguments
        RTValue generator = rootNode.getArgValue();
        RTValue currentRootNode;
        RTValue record = (currentRootNode = rootNode.prevArg()).getArgValue();
        RTValue recordDictionaryThunk = currentRootNode.prevArg().getArgValue();

        // Release the fields in the root node to open them to garbage collection
        rootNode.clearMembers();

        return f3S (
                RTValue.lastRef(recordDictionaryThunk.evaluate($ec), recordDictionaryThunk = null),
                RTValue.lastRef(record.evaluate($ec), record = null),
                RTValue.lastRef(generator.evaluate($ec), generator = null),
                $ec);
    }

    @Override
    public final RTValue f3L(RTValue recordDictionary, RTValue record, RTValue generator, RTExecutionContext $ec) throws CALExecutorException {

        return f3S (
                RTValue.lastRef(recordDictionary.evaluate($ec), recordDictionary = null),
                RTValue.lastRef(record.evaluate($ec), record = null),
                RTValue.lastRef(generator.evaluate($ec), generator = null),
                $ec);
    }

    @Override
    public final RTValue f3S(RTValue recordDictionary, RTValue record, RTValue generator, RTExecutionContext $ec) throws CALExecutorException {
        $ec.incrementNMethodCalls();

        /*
         * loop through each element in the input record and apply the coarbitrary
         * method to vary the input generator so that the resulting generator will be
         * dependant on each of the fields in the input record.
         */
        final RTRecordValue recordDict = (RTRecordValue)recordDictionary;
        final RTRecordValue inputRecord = (RTRecordValue) record;
        RTValue result = generator;
        for (int i = 0, nFields = recordDict.getNFields(); i < nFields; ++i) {
            RTValue elem = inputRecord.getNthValue(i);
            RTValue fieldDict = recordDict.getNthValue(i);
            result = fieldDict.apply( indexOfCoArbitraryClassMethod, elem, result).evaluate($ec);
       }

        return result;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public final String getModuleName () {
        //JUnit tested to equal its binding file value in RuntimeStringConstantsTest.
        return "Cal.Utilities.QuickCheck";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getUnqualifiedName () {
        //JUnit tested to equal its binding file value in RuntimeStringConstantsTest.
        return "coarbitraryRecordPrimitive";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getQualifiedName() {
        //JUnit tested to equal its binding file value in RuntimeStringConstantsTest.
        return "Cal.Utilities.QuickCheck.coarbitraryRecordPrimitive";
    }

}
