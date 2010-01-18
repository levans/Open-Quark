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
 * RTResultFunction.java
 * Created: Jan 22, 2002 at 2:16:12 PM
 * By: Raymond Cypher
 */

package org.openquark.cal.internal.runtime.lecc;

import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.runtime.CalValue;

/**
 * @author Raymond Cypher
 *
 * This class is the root class for functional nodes that need
 * a pointer for an assigned result.
 */
public abstract class RTResultFunction extends RTFunction {

    /**  The result, once evaluated (otherwise this can be an application chain for functions) */
    protected RTValue result;

    /** Back pointer used to avoid indirection chains. */
    private RTResultFunction parent;

    /** true if runtime stats are being generated, which results in a slower evaluate method. */
    private static final boolean HAS_RUNTIME_STATS =
         LECCMachineConfiguration.generateStatistics();

    public final void setResult (RTValue res) {
        res = res.getValue();
        if (res != this) {
            result = res;
            if (parent == null) {
                if (res instanceof RTResultFunction) {
                    ((RTResultFunction)res).setParent(this);
                }
            } else {
                parent.setResult(res);
            }
        }
    }

    /**
     * Get the value without forcing evaluation.
     * Try to follow any indirection chains to
     * their conclusion.
     * @return RTValue the value
     */
    @Override
    public RTValue getValue() {
        if (result == null) {
            return this;
        }
        return (result = result.getValue());
    }

    /**
     * Return the evaluated RTValue.  This may cause the value to be evaluated
     * if this hasn't already been done.  The memoised result is returned.
     * @param ec
     * @return RTValue the memoised result of evaluating this RTValue
     * @throws CALExecutorException
     */
    @Override
    public RTValue evaluate(RTExecutionContext ec) throws CALExecutorException {
       if (LECCMachineConfiguration.concurrentRuntime()) {
           return synchronizedEvaluate(ec);
       }

       return unsynchronizedEvaluate(ec);
    }

    //WARNING: the implementation of this method must be kept compatible with synchronizedEvaluate
    private final RTValue unsynchronizedEvaluate(RTExecutionContext ec) throws CALExecutorException {
        if (!LECCMachineConfiguration.nonInterruptibleRuntime() && ec.isQuitRequested()) {
            throw RTValue.INTERRUPT_EXCEPTION;
        }

        RTValue newResult = result == null ? this : result;
        RTValue lastResult;

        //do not repeatedly poll the SourceGenerationConfiguration settings in the most performance important common case
        //where we are not generating any runtime statistics

        if (RTResultFunction.HAS_RUNTIME_STATS) {

            // Attempt to reduce this result
            do {
                if (LECCMachineConfiguration.generateStatistics()) {
                    ec.incrementNReductions();
                }

                lastResult = newResult;
                newResult = newResult.reduce(ec);

            } while (lastResult != newResult);

        } else {

            // Attempt to reduce this result
            do {
                lastResult = newResult;
                newResult = newResult.reduce(ec);
            } while (lastResult != newResult);
        }

        if (newResult != this) {
            setResult (newResult);
        }

        return newResult;
    }

    //WARNING: the implementation of this method must be kept compatible with unsynchronizedEvaluate
    synchronized private final RTValue synchronizedEvaluate(RTExecutionContext ec) throws CALExecutorException {
        if (!LECCMachineConfiguration.nonInterruptibleRuntime() && ec.isQuitRequested()) {
            throw RTValue.INTERRUPT_EXCEPTION;
        }

        RTValue newResult = result == null ? this : result;
        RTValue lastResult;

        //do not repeatedly poll the SourceGenerationConfiguration settings in the most performance important common case
        //where we are not generating any runtime statistics

        if (RTResultFunction.HAS_RUNTIME_STATS) {

            // Attempt to reduce this result
            do {
                if (LECCMachineConfiguration.generateStatistics()) {
                    ec.incrementNReductions();
                }

                lastResult = newResult;
                newResult = newResult.synchronizedReduce(ec);

            } while (lastResult != newResult);

        } else {

            // Attempt to reduce this result
            do {
                lastResult = newResult;
                newResult = newResult.synchronizedReduce(ec);
            } while (lastResult != newResult);
        }

        if (newResult != this) {
            setResult (newResult);
        }

        return newResult;
    }

    @Override
    synchronized final protected RTValue synchronizedReduce(RTExecutionContext ec) throws CALExecutorException {
        return reduce(ec);
    }

    /**
     * Set the parent reference.  This allows the root of the graph to be
     * updated as each intermediate result is further reduced. This avoids
     * building up long indirection chains.
     * @param parent
     */
    private final void setParent (final RTResultFunction parent) {
        this.parent = parent;
    }

    /**
     * Release any held graph nodes.
     */
    public abstract void clearMembers();

    /**
     * The method returns the argument value i.e. right hand branch of
     * a general application node.
     * @return the right hand branch as appropriate
     */
    @Override
    public RTValue getArgValue () {
        return result.getArgValue();
    }

    /**
     * Return the previous argument (until we reach the root)
     * @return the previous argument
     */
    @Override
    public RTValue prevArg() {
        // Note:  In the normal course of graph reduction this method will not be
        // called before result is set.  An explicit check for a null result value
        // is omitted for performance reasons.
        return result.prevArg();
    }

    /**
     * Function used for generalized traversal to the
     * left hand side of a program graph.
     * @return the next graph node to the left, null if there is nothing to the left.
     * {@inheritDoc}
     */
    @Override
    RTValue lhs () {
        if(result != null) {
            return result;
        }
        return null;
    }



    /**
     * {@inheritDoc}
     */
    @Override
    public int debug_getNChildren() {
        if (result == null) {
            throw new IllegalStateException("should call the overidden version if result == null");
        }
        return 1;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public CalValue debug_getChild(int childN) {
        if (result == null) {
            throw new IllegalStateException("should call the overidden version if result == null");
        }

        if (childN == 0) {
            return result;
        }

        throw new IndexOutOfBoundsException();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String debug_getNodeStartText() {
        if (result == null) {
            throw new IllegalStateException("should call the overidden version if result == null");
        }

        return "";
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String debug_getNodeEndText() {
        if (result == null) {
            throw new IllegalStateException("should call the overidden version if result == null");
        }

        return "";
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String debug_getChildPrefixText(int childN) {
        if (result == null) {
            throw new IllegalStateException("should call the overidden version if result == null");
        }

        if (childN == 0) {
            return "";
        }
        throw new IndexOutOfBoundsException();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean debug_isIndirectionNode() {
        return result != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final DataType getDataType() {
        if (result != null) {
            return result.getDataType();
        } else {
            return DataType.OTHER;
        }
    }
}

