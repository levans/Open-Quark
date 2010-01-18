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
 * RTApplication.java
 * Created: May 22, 2003 11:24:48 AM
 * By: RCypher
 */

package org.openquark.cal.internal.runtime.lecc;

import java.util.concurrent.atomic.AtomicInteger;

import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.runtime.CalValue;

/**
 * This is the RTApplication class/interface.
 * It represents an application in one argument.
 * <p>
 * Created: May 22, 2003 11:24:48 AM
 * @author RCypher
 */
public class RTApplication extends RTResultFunction {

    /** The function to which this is applied */
    RTValue function;  
    RTValue argument;

    /** number of instances of this class. Used for statistics purposes. */
    private static final AtomicInteger nInstances = new AtomicInteger(0);

    RTApplication(RTValue f, RTValue argument) {
        assert (f != null && argument != null) : "Invalid argument value in RTApplication constructor.";

        // Create a new application of f
        // f is referred to as a part of the identify of this value until
        // this is reduced, when it is replaced by the result value
        this.argument = argument;
        this.function = f;
        if (LECCMachineConfiguration.generateAppCounts()) {
            nInstances.incrementAndGet();
        }
    }
    
    public static final int getNInstances() {
        return nInstances.get();
    }
    
    public static final void resetNInstances() {
        nInstances.set(0);
    }

    /**
     * If this node is not an indirection (i.e. result is null) than the arg
     * count will be 1.  Other wise return zero since an indirection
     * has no arguments itself.
     *
     * @see org.openquark.cal.internal.runtime.lecc.RTValue#getArgCount()
     */
    @Override
    public int getArgCount() {
        if (result == null) {
            return 1;
        }

        return 0;
    }


    /**
     * {@inheritDoc}
     */
     @Override
    protected RTValue reduce(RTExecutionContext ec) throws CALExecutorException {
        // If function != null it means this hasn't been reduce. i.e. there isn't
        // yet a result.
        if (function != null) {

            // First we want to walk to the left hand side of the
            // current graph.  As we traverse the nodes to the
            // left we accumulate the number of arguments.
            RTValue lastArg = this;
            int argCount = getArgCount();
            RTValue rv = this;
            for (RTValue lhs = rv.lhs(); lhs != null; lhs = rv.lhs()) {
                rv = lhs;
                argCount += rv.getArgCount();
            }

            // Now that we have the left hand side of the current graph (i.e. rv)
            // we can compare the arity to the number of arguments.
            int arity;

            while (argCount > (arity = rv.getArity())) {

                // There are more arguments than required.  Move to the left
                // until the sub-graph represents a fully saturated application.
                for (int i = 0; i < argCount - arity; i++) {
                    lastArg = lastArg.prevArg();
                }

                // Evaluate the sub-graph.
                lastArg.evaluate (ec);

                // Now we need to re-do the walk to the left, since the graph has been
                // modified by the evaluation.  We re-calculate the argument count as
                // we go.
                lastArg = this;
                rv = this;
                argCount = getArgCount();
                for (RTValue lhs = rv.lhs(); lhs != null; lhs = rv.lhs()) {
                    rv = lhs;
                    argCount += rv.getArgCount();
                }
            }

            // At this point we know that we won't have too many arguments.
            // However we still might have too few.  If so there is nothing
            // we can do except return.
            if (argCount < arity) {
                return this;
            }

            // Execute the function logic of the leftmost node by calling the 'f'
            // method.
            setResult (rv.f (this, ec));

        } else if (result == null) {
            throw new NullPointerException ("Invalid reduction state in application.  This is probably caused by a circular function definition.");
        }

        return (result);
    }

    /**
     * Return the previous argument (until we reach the root).
     * This function follows the graph to the left until it reaches
     * a node which is not an indirection or which has no left hand size.
     * @return RTValue the previous argument
     */
    @Override
    public RTValue prevArg() {
        if (result != null) {
            return result.prevArg();
        }
        return function;
    }

    /**
     * If this node is not an indirection (i.e. result is null)
     * then the arg value will be the right hand side.  Otherwise
     * we need to follow the indirection chain.
     * @return the argument (i.e. right side) of this application.
     */
    @Override
    public RTValue getArgValue () {
        if (result != null) {
            return result.getArgValue ();
        }
        return argument;
    }

    /*
     *  (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.RTResultFunction#clearMembers()
     */
    @Override
    public void clearMembers () {
        function = null;
        argument = null;
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
            return function;
        case 1:
            return argument;
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

        return "(";
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

        switch (childN) {
        case 0:
            return "";
        case 1:
            return " ";
        default:
            throw new IndexOutOfBoundsException();
        }
    }

    /**
     * Function used for generalized traversal to the
     * left hand side of a program graph.
     * This function will return the graph node immediately to the left
     * or null if there is none.
     * @return the next graph node to the left, null if there is nothing to the left.
     * {@inheritDoc}
     */
    @Override
    RTValue lhs() {
        if (result != null) {
            return result;
        }
        return function;
    }
}

