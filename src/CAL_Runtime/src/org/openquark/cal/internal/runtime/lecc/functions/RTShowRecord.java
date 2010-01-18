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
 * RTShowRecord.java
 * Created: Mar 31, 2005
 * By: Bo Ilic
 */

package org.openquark.cal.internal.runtime.lecc.functions;

import org.openquark.cal.internal.runtime.lecc.LECCMachineConfiguration;
import org.openquark.cal.internal.runtime.lecc.RTData;
import org.openquark.cal.internal.runtime.lecc.RTExecutionContext;
import org.openquark.cal.internal.runtime.lecc.RTRecordValue;
import org.openquark.cal.internal.runtime.lecc.RTResultFunction;
import org.openquark.cal.internal.runtime.lecc.RTSupercombinator;
import org.openquark.cal.internal.runtime.lecc.RTValue;
import org.openquark.cal.runtime.CALExecutorException;



/**
 * Implements the built-in primitive function:
 * showRecord :: Show r => {r} -> String
 * defined in the Debug module.
 *
 * @author Bo Ilic
 */
public final class RTShowRecord extends RTSupercombinator {

    public static final RTShowRecord $instance = new RTShowRecord();

    private RTShowRecord() {
        // Declared private to limit instantiation.
    }

    public static final RTShowRecord make(RTExecutionContext $ec) {
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
        // Because the function returns a string value in HNF we can delegate to the unboxed return version of the function.
        return RTData.CAL_String.make(fUnboxed2S(recordDictionary, x, $ec));
    }

    /**
     * This is the version of the function logic that directly returns an unboxed value.
     * All functions whose return types can be unboxed should have a version of the function
     * logic which returns an unboxed value.
     * @param recordDictionary
     * @param x
     * @param $ec
     * @return String
     * @throws CALExecutorException
     */
    public final String fUnboxed2S(RTValue recordDictionary, RTValue x, RTExecutionContext $ec) throws CALExecutorException {
        $ec.incrementNMethodCalls();
        if (LECCMachineConfiguration.generateDebugCode() && $ec.isDebugProcessingNeeded(getQualifiedName())) {
            $ec.debugProcessing(getQualifiedName(), new RTValue[]{recordDictionary, x});
        }

        //showRecord recordDictionary x.
        //the compiler ensures that the 2 record arguments all have the same fields.
        //must iterate in a deterministic order over the field names (as specified by FieldName.CalSourceFormComparator)
        //so that the function is well-defined in the presence of side effects.
        //If f is a field, then recordDictionary.f is the dictionary for use when calling the class method Debug.show on
        //the value x.f.

        RTRecordValue recordDict = (RTRecordValue)recordDictionary;
        RTRecordValue xRecord = (RTRecordValue)x;

        int nFields = recordDict.getNFields();
        StringBuilder showResult;

        //tuple records (with 2 or more fields) are displayed using the parentheses notation where field-names are omitted.
        if (recordDict.isTuple2OrMoreRecord()) {

            showResult = new StringBuilder("(");
            for (int i = 0; i < nFields; ++i) {

                if (i > 0) {
                    showResult.append(", ");
                }

                RTValue fieldDict = recordDict.getNthValue(i);
                RTValue xField = xRecord.getNthValue(i);

                //compute "Debug.show fieldDict xField"
                //this is just (after inlining Debug.show d = d)
                //fieldDict xField

                showResult.append(fieldDict.apply(xField).evaluate($ec).getStringValue());
            }

            showResult.append(")");

        } else {

            showResult = new StringBuilder("{");
            for (int i = 0; i < nFields; ++i) {

                if (i > 0) {
                    showResult.append(", ");
                }

                RTValue fieldDict = recordDict.getNthValue(i);
                RTValue xField = xRecord.getNthValue(i);

                //compute "Debug.show fieldDict xField"
                //this is just (after inlining Debug.show d = d)
                //fieldDict xField

                showResult.append(xRecord.getNthFieldName(i)).append(" = ");
                showResult.append(fieldDict.apply(xField).evaluate($ec).getStringValue());
            }

            showResult.append("}");
        }

        return showResult.toString();
    }

   /**
    * {@inheritDoc}
    */
   @Override
   public final String getModuleName () {
       //JUnit tested to equal its binding file value in RuntimeStringConstantsTest.
       return "Cal.Core.Debug";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public final String getUnqualifiedName () {
       //JUnit tested to equal its binding file value in RuntimeStringConstantsTest.
       return "showRecord";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public final String getQualifiedName() {
       //JUnit tested to equal its binding file value in RuntimeStringConstantsTest.
       return "Cal.Core.Debug.showRecord";
   }
}