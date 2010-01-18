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
 * RTRecordTypeDictionary.java
 * Created: Oct 26/2005
 * By: James Wright
 */
package org.openquark.cal.internal.runtime.lecc.functions;

import org.openquark.cal.internal.runtime.RecordType;
import org.openquark.cal.internal.runtime.lecc.LECCMachineConfiguration;
import org.openquark.cal.internal.runtime.lecc.RTData;
import org.openquark.cal.internal.runtime.lecc.RTExecutionContext;
import org.openquark.cal.internal.runtime.lecc.RTResultFunction;
import org.openquark.cal.internal.runtime.lecc.RTSupercombinator;
import org.openquark.cal.internal.runtime.lecc.RTValue;
import org.openquark.cal.runtime.CALExecutorException;



/**
 * Implements the built-in primitive function:
 *
 *      recordTypeDictionary :: Typeable r => {r} -> Prelude.CalValue
 *
 * This function just returns its own dictionary argument to the caller as an
 * opaque Java object.  The CAL program can then call RecordType methods on the
 * dictionary to query type information about the record efficiently.
 *
 * @author James Wright
 */
public final class RTRecordTypeDictionary extends RTSupercombinator {

    public static final RTRecordTypeDictionary $instance = new RTRecordTypeDictionary();


    private RTRecordTypeDictionary() {
        // Declared private to limit instantiation.
    }

    public static final RTRecordTypeDictionary make(RTExecutionContext $ec) {
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

        return f2S(
                RTValue.lastRef(recordDictionary, recordDictionary = null),
                RTValue.lastRef(x, x = null),
                $ec);
    }

    @Override
    public final RTValue f2L(RTValue recordDictionary, RTValue x, RTExecutionContext $ec) throws CALExecutorException {

        return f2S(
                RTValue.lastRef(recordDictionary, recordDictionary = null),
                RTValue.lastRef(x, x = null),
                $ec);
    }

    @Override
    public final RTValue f2S(RTValue recordDictionary, RTValue x, RTExecutionContext $ec) {
        // We can simply delegate to the unboxed return version of the function.
        return RTData.CAL_Opaque.make(fUnboxed2S (recordDictionary, x, $ec));
    }

    public final RecordType fUnboxed2S (RTValue recordDictionary, RTValue xThunk, RTExecutionContext $ec) {
        $ec.incrementNMethodCalls();
        if (LECCMachineConfiguration.generateDebugCode() && $ec.isDebugProcessingNeeded(getQualifiedName())) {
            $ec.debugProcessing(getQualifiedName(), new RTValue[]{recordDictionary, xThunk});
        }

        //recordTypeDictionary recordDictionary x
        //recordDictionary is {fieldName1 = typeOfFieldName1, fieldName2 = typeOfFieldName2, ...}

        return (RecordType)recordDictionary;
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
        return "recordTypeDictionary";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getQualifiedName() {
        //JUnit tested to equal its binding file value in RuntimeStringConstantsTest.
        return "Cal.Core.Prelude.recordTypeDictionary";
    }
}
