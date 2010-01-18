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
 * RTDeepSeq.java
 * Created: July 12, 2005
 * By: Greg McClement
 */

package org.openquark.cal.internal.runtime.lecc.functions;

import org.openquark.cal.internal.runtime.lecc.LECCMachineConfiguration;
import org.openquark.cal.internal.runtime.lecc.RTExecutionContext;
import org.openquark.cal.internal.runtime.lecc.RTFullApp;
import org.openquark.cal.internal.runtime.lecc.RTResultFunction;
import org.openquark.cal.internal.runtime.lecc.RTSupercombinator;
import org.openquark.cal.internal.runtime.lecc.RTValue;
import org.openquark.cal.internal.runtime.lecc.RTData.CAL_String;
import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.runtime.CalValue;
import org.openquark.cal.runtime.ErrorInfo;


/**
 * Implements the built-in primitive function:
 * error :: String -> a
 * defined in the Prelude module.
 * This is the error function with an extra hidden argument that contains the source position.
 *
 * The returned value is an RTValue.
 *
 * @author Greg McClement
 */

public final class RTError extends RTSupercombinator {
    public static final RTError $instance = new RTError();

    private RTError() {
        // Empty constructor created for access control.
    }

    public static final RTError make(RTExecutionContext $ec) {
        return RTError.$instance;
    }

    @Override
    public int getArity() {
        return 2;
    }

    @Override
    public final RTValue f(final RTResultFunction rootNode, final RTExecutionContext $ec) throws CALExecutorException {
        // Arguments
        RTValue errorMessage = rootNode.getArgValue();
        RTValue errorInfo = rootNode.prevArg().getArgValue();

        // Release the fields in the root node to open them to garbage collection
        rootNode.clearMembers();

        return f2S(
                RTValue.lastRef(errorInfo, errorInfo = null),
                RTError.lastRef(errorMessage.evaluate($ec).getStringValue(), errorMessage = null),
                $ec);
    }

    @Override
    public final RTValue f2L(RTValue errorInfo, RTValue message, RTExecutionContext $ec) throws CALExecutorException {
        return f2S(
                RTValue.lastRef(errorInfo, errorInfo = null),
                RTError.lastRef(message.evaluate($ec).getStringValue(), message = null),
                $ec);
    }

    public final RTValue f2S(RTValue errorInfo, String message, RTExecutionContext $ec) throws CALExecutorException {

        $ec.incrementNMethodCalls();

        if (LECCMachineConfiguration.generateDebugCode() && $ec.isDebugProcessingNeeded(getQualifiedName())) {
            $ec.debugProcessing(getQualifiedName(), new RTValue[]{errorInfo, CAL_String.make(message)});
        }

        // Top level supercombinator logic
        return RTValue.errorCall((ErrorInfo) errorInfo.getOpaqueValue(), message);
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
        return "error";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getQualifiedName() {
        //JUnit tested to equal its binding file value in RuntimeStringConstantsTest.
        return "Cal.Core.Prelude.error";
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
     * @param s
     * @param v
     * @return The value of the first argument.
     */
    private static final String lastRef (String s, RTValue v) {
        return s;
    }

    /**
     * A fully saturated application of error.
     * Note:  This class doesn't need to implement getArity() or getArgCount()
     * as a fully saturated application can be treated like a zero arity function.
     * @author rcypher
     */
    public static final class RTAppS extends RTFullApp {
        private final RTError function;
        private RTValue errorInfo;
        private String message;

        public RTAppS(RTError function, RTValue errorInfo, String message) {
            assert (function != null && errorInfo != null && message != null) :
                "Null argument in constructor for RTError.RTAppS.";

            this.function = function;
            this.errorInfo = errorInfo;
            this.message = message;
        }

        @Override
        protected final RTValue reduce(RTExecutionContext $ec) throws CALExecutorException {
            if (result == null) {
                setResult(function.f2S(errorInfo, message, $ec));
                clearMembers();
            }
            return result;
        }

        @Override
        public final void clearMembers() {
            //we don't need to clear this.function, since it points to a singleton held by a static member field
            errorInfo = null;
            message = null;
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
                return errorInfo;
            case 1:
                return CAL_String.make(message);
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
