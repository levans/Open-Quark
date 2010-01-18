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
 * RTFullApp.java
 * Created: Mar 30, 2004
 * By: RCypher
 */

package org.openquark.cal.internal.runtime.lecc;

import java.util.concurrent.atomic.AtomicInteger;

import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.runtime.CalValue;

/**
 * RTFullApp is a base class for representing application chains to a supercombinator where
 * the specific supercombinator is known at code generation time and the number of arguments
 * matches the arity of the supercombinator.
 *
 * A full application node can be treated like an SC of arity zero with regards to the arity and
 * arg count of the node since the SC arity will always match the number of args held by the
 * node.
 *
 * Fully saturated application nodes are implemented for SCs of up to 15 arguments.
 * The advantages of fully saturated application nodes are:
 * 1) fewer individual memory allocations.  Using general application nodes a fully
 * saturated application of arity 4 would require four allocations of an application node.
 * With fully saturated application nodes there is only one allocation.
 * 2) When reducing and traversing the graph it is more efficient to have a single node
 * to examine than multiple nodes.
 *
 * @author RCypher
 * Created: Mar 30, 2004
 *
 *
 */
public abstract class RTFullApp extends RTResultFunction {
    
    /** number of instances of this class. Used for statistics purposes. */
    private static final AtomicInteger nInstances = new AtomicInteger(0);

    public RTFullApp() {
        if (LECCMachineConfiguration.generateAppCounts()) {
            nInstances.incrementAndGet();
        }
    }
    
    public static int getNInstances() {
        return nInstances.get();
    }

    public static void resetNInstances() {
        nInstances.set(0);
    }  

    /**
     * For purposes of graph reduction a fully saturated application
     * can be treated as having zero arity and zero arguments.  By
     * having these two methods simply return zero we can do a final
     * implementation of the RTFullApp base class and don't need to
     * generate them in the generated, SC specific, application node
     * classes.
     * @return the arity of this graph node, always zero.
     */
    @Override
    public final int getArity() {
        return 0;
    }

    /**
     * For purposes of graph reduction a fully saturated application
     * can be treated as having zero arity and zero arguments.  By
     * having these two methods simply return zero we can do a final
     * implementation of the RTFullApp base class and don't need to
     * generate them in the generated, SC specific, application node
     * classes.
     * @return the arg count of this graph node, always zero.
     */
    @Override
    public final int getArgCount() {
        return 0;
    }

    /**
     * The General class is used as the base class for fully saturated application nodes
     * where is doesn't matter what the specific SC is.  i.e. we know that it is a
     * supercombinator of a specific arity.
     * @author rcypher
     */
    public static abstract class General extends RTFullApp {

        /** The function being applied. */
        final RTSupercombinator function;

        General(RTSupercombinator function) {
            assert (function != null) : "Invalid value for 'function' in RTFullApp.General constructor.";
            this.function = function;
        }


        /**
         * WARNING- this class was automatically generated by FullAppNodeGenerator. DO NOT MANUALLY EDIT THIS CLASS.
         *
         * Representation of an arity zero function applied to nothing.
         * This may sound silly but it keeps things simple as zero arity
         * functions and CAFs can be derived from RTSupercombinator like
         * all other functions and the _0 class handles indirecting to
         * a result.
         * Creation: Aug. 3, 2006
         */
        public static final class _0 extends General {


            public _0 (RTSupercombinator function) {
                super (function);

            }

            /*
             *  (non-Javadoc)
             * @see org.openquark.cal.internal.runtime.lecc.RTResultFunction#clearMembers()
             */
            @Override
            public final void clearMembers() {
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public final int debug_getNChildren() {
                if (result != null) {
                    return super.debug_getNChildren();
                }
                return 0;
            }
            /**
             * {@inheritDoc}
             */
            @Override
            public final CalValue debug_getChild(int childN) {
                if (result != null) {
                    return super.debug_getChild(childN);
                }

                throw new IndexOutOfBoundsException();
            }
            /**
             * {@inheritDoc}
             */
            @Override
            public final String debug_getNodeStartText() {
                if (result != null) {
                    return super.debug_getNodeStartText();
                }

                return function.getQualifiedName();
            }
            /**
             * {@inheritDoc}
             */
            @Override
            public final String debug_getNodeEndText() {
                if (result != null) {
                    return super.debug_getNodeEndText();
                }

                return "";
            }
            /**
             * {@inheritDoc}
             */
            @Override
            public final String debug_getChildPrefixText(int childN) {
                if (result != null) {
                    return super.debug_getChildPrefixText(childN);
                }

                if (childN >= 0 && childN < 0) {
                    return " ";
                }

                throw new IndexOutOfBoundsException();
            }

                /**
                 * {@inheritDoc}
                 */
                @Override
                protected final RTValue reduce(RTExecutionContext ec)
                        throws CALExecutorException {
                    // Reduce from this application
                    // Update and return result
                        setResult(function.f(
                            this,
                            ec));
                    return (result);
                }
        }

        /**
         * WARNING- this class was automatically generated by FullAppNodeGenerator. DO NOT MANUALLY EDIT THIS CLASS.
         *
         * This class is used to represent a full application of an
         * arity 1 function.
         */
        public static abstract class _1 extends General {

            RTValue arg1;

            public _1 (RTSupercombinator function, RTValue arg1) {
                super (function);
                assert (arg1 != null) : "Invalid argument value in RTFullApp.General._1 constructor.";

                this.arg1 = arg1;
            }

            /*
             *  (non-Javadoc)
             * @see org.openquark.cal.internal.runtime.lecc.RTResultFunction#clearMembers()
             */
            @Override
            public final void clearMembers() {
                arg1 = null;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public final int debug_getNChildren() {
                if (result != null) {
                    return super.debug_getNChildren();
                }
                return 1;
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
                    return arg1;
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

                if (childN >= 0 && childN < 1) {
                    return " ";
                }

                throw new IndexOutOfBoundsException();
            }

            public static final class _L extends _1 {
                public _L(RTSupercombinator function, RTValue arg1) {
                        super(function, arg1);
                }
                /**
                 * {@inheritDoc}
                 */
                @Override
                protected final RTValue reduce(RTExecutionContext ec)
                        throws CALExecutorException {
                    // Reduce from this application
                    // Update and return result
                    if (arg1 != null) {
                        setResult(function.f1L(
                            RTValue.lastRef(arg1, (arg1 = null)),
                            ec));
                    } else if (result == null) {
                        throw new NullPointerException(
                                "Invalid reduction state in application.  This is probably caused by a circular function definition.");
                    }
                    return (result);
                }
            }
            public static final class _S extends _1 {
                public _S(RTSupercombinator function, RTValue arg1) {
                        super(function, arg1);
                }
                /**
                 * {@inheritDoc}
                 */
                @Override
                protected final RTValue reduce(RTExecutionContext ec)
                        throws CALExecutorException {
                    // Reduce from this application
                    // Update and return result
                    if (arg1 != null) {
                        setResult(function.f1S(
                            RTValue.lastRef(arg1, (arg1 = null)),
                            ec));
                    } else if (result == null) {
                        throw new NullPointerException(
                                "Invalid reduction state in application.  This is probably caused by a circular function definition.");
                    }
                    return (result);
                }
            }
        }

        /**
         * WARNING- this class was automatically generated by FullAppNodeGenerator. DO NOT MANUALLY EDIT THIS CLASS.
         *
         * This class is used to represent a full application of an
         * arity 2 function.
         */
        public static abstract class _2 extends General {

            RTValue arg1;
            RTValue arg2;

            public _2 (RTSupercombinator function, RTValue arg1, RTValue arg2) {
                super (function);
                assert (arg1 != null && arg2 != null) : "Invalid argument value in RTFullApp.General._2 constructor.";

                this.arg1 = arg1;
                this.arg2 = arg2;
            }

            /*
             *  (non-Javadoc)
             * @see org.openquark.cal.internal.runtime.lecc.RTResultFunction#clearMembers()
             */
            @Override
            public final void clearMembers() {
                arg1 = null;
                arg2 = null;
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
                    return arg1;
                case 1:
                    return arg2;
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

            public static final class _L extends _2 {
                public _L(RTSupercombinator function, RTValue arg1, RTValue arg2) {
                        super(function, arg1, arg2);
                }
                /**
                 * {@inheritDoc}
                 */
                @Override
                protected final RTValue reduce(RTExecutionContext ec)
                        throws CALExecutorException {
                    // Reduce from this application
                    // Update and return result
                    if (arg1 != null) {
                        setResult(function.f2L(
                            RTValue.lastRef(arg1, (arg1 = null)),
                            RTValue.lastRef(arg2, (arg2 = null)),
                            ec));
                    } else if (result == null) {
                        throw new NullPointerException(
                                "Invalid reduction state in application.  This is probably caused by a circular function definition.");
                    }
                    return (result);
                }
            }
            public static final class _S extends _2 {
                public _S(RTSupercombinator function, RTValue arg1, RTValue arg2) {
                        super(function, arg1, arg2);
                }
                /**
                 * {@inheritDoc}
                 */
                @Override
                protected final RTValue reduce(RTExecutionContext ec)
                        throws CALExecutorException {
                    // Reduce from this application
                    // Update and return result
                    if (arg1 != null) {
                        setResult(function.f2S(
                            RTValue.lastRef(arg1, (arg1 = null)),
                            RTValue.lastRef(arg2, (arg2 = null)),
                            ec));
                    } else if (result == null) {
                        throw new NullPointerException(
                                "Invalid reduction state in application.  This is probably caused by a circular function definition.");
                    }
                    return (result);
                }
            }
        }

        /**
         * WARNING- this class was automatically generated by FullAppNodeGenerator. DO NOT MANUALLY EDIT THIS CLASS.
         *
         * This class is used to represent a full application of an
         * arity 3 function.
         */
        public static abstract class _3 extends General {

            RTValue arg1;
            RTValue arg2;
            RTValue arg3;

            public _3 (RTSupercombinator function, RTValue arg1, RTValue arg2, RTValue arg3) {
                super (function);
                assert (arg1 != null && arg2 != null && arg3 != null) : "Invalid argument value in RTFullApp.General._3 constructor.";

                this.arg1 = arg1;
                this.arg2 = arg2;
                this.arg3 = arg3;
            }

            /*
             *  (non-Javadoc)
             * @see org.openquark.cal.internal.runtime.lecc.RTResultFunction#clearMembers()
             */
            @Override
            public final void clearMembers() {
                arg1 = null;
                arg2 = null;
                arg3 = null;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public final int debug_getNChildren() {
                if (result != null) {
                    return super.debug_getNChildren();
                }
                return 3;
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
                    return arg1;
                case 1:
                    return arg2;
                case 2:
                    return arg3;
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

                if (childN >= 0 && childN < 3) {
                    return " ";
                }

                throw new IndexOutOfBoundsException();
            }

            public static final class _L extends _3 {
                public _L(RTSupercombinator function, RTValue arg1, RTValue arg2, RTValue arg3) {
                        super(function, arg1, arg2, arg3);
                }
                /**
                 * {@inheritDoc}
                 */
                @Override
                protected final RTValue reduce(RTExecutionContext ec)
                        throws CALExecutorException {
                    // Reduce from this application
                    // Update and return result
                    if (arg1 != null) {
                        setResult(function.f3L(
                            RTValue.lastRef(arg1, (arg1 = null)),
                            RTValue.lastRef(arg2, (arg2 = null)),
                            RTValue.lastRef(arg3, (arg3 = null)),
                            ec));
                    } else if (result == null) {
                        throw new NullPointerException(
                                "Invalid reduction state in application.  This is probably caused by a circular function definition.");
                    }
                    return (result);
                }
            }
            public static final class _S extends _3 {
                public _S(RTSupercombinator function, RTValue arg1, RTValue arg2, RTValue arg3) {
                        super(function, arg1, arg2, arg3);
                }
                /**
                 * {@inheritDoc}
                 */
                @Override
                protected final RTValue reduce(RTExecutionContext ec)
                        throws CALExecutorException {
                    // Reduce from this application
                    // Update and return result
                    if (arg1 != null) {
                        setResult(function.f3S(
                            RTValue.lastRef(arg1, (arg1 = null)),
                            RTValue.lastRef(arg2, (arg2 = null)),
                            RTValue.lastRef(arg3, (arg3 = null)),
                            ec));
                    } else if (result == null) {
                        throw new NullPointerException(
                                "Invalid reduction state in application.  This is probably caused by a circular function definition.");
                    }
                    return (result);
                }
            }
        }

        /**
         * WARNING- this class was automatically generated by FullAppNodeGenerator. DO NOT MANUALLY EDIT THIS CLASS.
         *
         * This class is used to represent a full application of an
         * arity 4 function.
         */
        public static abstract class _4 extends General {

            RTValue arg1;
            RTValue arg2;
            RTValue arg3;
            RTValue arg4;

            public _4 (RTSupercombinator function, RTValue arg1, RTValue arg2, RTValue arg3, RTValue arg4) {
                super (function);
                assert (arg1 != null && arg2 != null && arg3 != null && arg4 != null) : "Invalid argument value in RTFullApp.General._4 constructor.";

                this.arg1 = arg1;
                this.arg2 = arg2;
                this.arg3 = arg3;
                this.arg4 = arg4;
            }

            /*
             *  (non-Javadoc)
             * @see org.openquark.cal.internal.runtime.lecc.RTResultFunction#clearMembers()
             */
            @Override
            public final void clearMembers() {
                arg1 = null;
                arg2 = null;
                arg3 = null;
                arg4 = null;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public final int debug_getNChildren() {
                if (result != null) {
                    return super.debug_getNChildren();
                }
                return 4;
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
                    return arg1;
                case 1:
                    return arg2;
                case 2:
                    return arg3;
                case 3:
                    return arg4;
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

                if (childN >= 0 && childN < 4) {
                    return " ";
                }

                throw new IndexOutOfBoundsException();
            }

            public static final class _L extends _4 {
                public _L(RTSupercombinator function, RTValue arg1, RTValue arg2, RTValue arg3, RTValue arg4) {
                        super(function, arg1, arg2, arg3, arg4);
                }
                /**
                 * {@inheritDoc}
                 */
                @Override
                protected final RTValue reduce(RTExecutionContext ec)
                        throws CALExecutorException {
                    // Reduce from this application
                    // Update and return result
                    if (arg1 != null) {
                        setResult(function.f4L(
                            RTValue.lastRef(arg1, (arg1 = null)),
                            RTValue.lastRef(arg2, (arg2 = null)),
                            RTValue.lastRef(arg3, (arg3 = null)),
                            RTValue.lastRef(arg4, (arg4 = null)),
                            ec));
                    } else if (result == null) {
                        throw new NullPointerException(
                                "Invalid reduction state in application.  This is probably caused by a circular function definition.");
                    }
                    return (result);
                }
            }
            public static final class _S extends _4 {
                public _S(RTSupercombinator function, RTValue arg1, RTValue arg2, RTValue arg3, RTValue arg4) {
                        super(function, arg1, arg2, arg3, arg4);
                }
                /**
                 * {@inheritDoc}
                 */
                @Override
                protected final RTValue reduce(RTExecutionContext ec)
                        throws CALExecutorException {
                    // Reduce from this application
                    // Update and return result
                    if (arg1 != null) {
                        setResult(function.f4S(
                            RTValue.lastRef(arg1, (arg1 = null)),
                            RTValue.lastRef(arg2, (arg2 = null)),
                            RTValue.lastRef(arg3, (arg3 = null)),
                            RTValue.lastRef(arg4, (arg4 = null)),
                            ec));
                    } else if (result == null) {
                        throw new NullPointerException(
                                "Invalid reduction state in application.  This is probably caused by a circular function definition.");
                    }
                    return (result);
                }
            }
        }

        /**
         * WARNING- this class was automatically generated by FullAppNodeGenerator. DO NOT MANUALLY EDIT THIS CLASS.
         *
         * This class is used to represent a full application of an
         * arity 5 function.
         */
        public static abstract class _5 extends General {

            RTValue arg1;
            RTValue arg2;
            RTValue arg3;
            RTValue arg4;
            RTValue arg5;

            public _5 (RTSupercombinator function, RTValue arg1, RTValue arg2, RTValue arg3, RTValue arg4, RTValue arg5) {
                super (function);
                assert (arg1 != null && arg2 != null && arg3 != null && arg4 != null && arg5 != null) : "Invalid argument value in RTFullApp.General._5 constructor.";

                this.arg1 = arg1;
                this.arg2 = arg2;
                this.arg3 = arg3;
                this.arg4 = arg4;
                this.arg5 = arg5;
            }

            /*
             *  (non-Javadoc)
             * @see org.openquark.cal.internal.runtime.lecc.RTResultFunction#clearMembers()
             */
            @Override
            public final void clearMembers() {
                arg1 = null;
                arg2 = null;
                arg3 = null;
                arg4 = null;
                arg5 = null;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public final int debug_getNChildren() {
                if (result != null) {
                    return super.debug_getNChildren();
                }
                return 5;
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
                    return arg1;
                case 1:
                    return arg2;
                case 2:
                    return arg3;
                case 3:
                    return arg4;
                case 4:
                    return arg5;
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

                if (childN >= 0 && childN < 5) {
                    return " ";
                }

                throw new IndexOutOfBoundsException();
            }

            public static final class _L extends _5 {
                public _L(RTSupercombinator function, RTValue arg1, RTValue arg2, RTValue arg3, RTValue arg4, RTValue arg5) {
                        super(function, arg1, arg2, arg3, arg4, arg5);
                }
                /**
                 * {@inheritDoc}
                 */
                @Override
                protected final RTValue reduce(RTExecutionContext ec)
                        throws CALExecutorException {
                    // Reduce from this application
                    // Update and return result
                    if (arg1 != null) {
                        setResult(function.f5L(
                            RTValue.lastRef(arg1, (arg1 = null)),
                            RTValue.lastRef(arg2, (arg2 = null)),
                            RTValue.lastRef(arg3, (arg3 = null)),
                            RTValue.lastRef(arg4, (arg4 = null)),
                            RTValue.lastRef(arg5, (arg5 = null)),
                            ec));
                    } else if (result == null) {
                        throw new NullPointerException(
                                "Invalid reduction state in application.  This is probably caused by a circular function definition.");
                    }
                    return (result);
                }
            }
            public static final class _S extends _5 {
                public _S(RTSupercombinator function, RTValue arg1, RTValue arg2, RTValue arg3, RTValue arg4, RTValue arg5) {
                        super(function, arg1, arg2, arg3, arg4, arg5);
                }
                /**
                 * {@inheritDoc}
                 */
                @Override
                protected final RTValue reduce(RTExecutionContext ec)
                        throws CALExecutorException {
                    // Reduce from this application
                    // Update and return result
                    if (arg1 != null) {
                        setResult(function.f5S(
                            RTValue.lastRef(arg1, (arg1 = null)),
                            RTValue.lastRef(arg2, (arg2 = null)),
                            RTValue.lastRef(arg3, (arg3 = null)),
                            RTValue.lastRef(arg4, (arg4 = null)),
                            RTValue.lastRef(arg5, (arg5 = null)),
                            ec));
                    } else if (result == null) {
                        throw new NullPointerException(
                                "Invalid reduction state in application.  This is probably caused by a circular function definition.");
                    }
                    return (result);
                }
            }
        }

        /**
         * WARNING- this class was automatically generated by FullAppNodeGenerator. DO NOT MANUALLY EDIT THIS CLASS.
         *
         * This class is used to represent a full application of an
         * arity 6 function.
         */
        public static abstract class _6 extends General {

            RTValue arg1;
            RTValue arg2;
            RTValue arg3;
            RTValue arg4;
            RTValue arg5;
            RTValue arg6;

            public _6 (RTSupercombinator function, RTValue arg1, RTValue arg2, RTValue arg3, RTValue arg4, RTValue arg5, RTValue arg6) {
                super (function);
                assert (arg1 != null && arg2 != null && arg3 != null && arg4 != null && arg5 != null && arg6 != null) : "Invalid argument value in RTFullApp.General._6 constructor.";

                this.arg1 = arg1;
                this.arg2 = arg2;
                this.arg3 = arg3;
                this.arg4 = arg4;
                this.arg5 = arg5;
                this.arg6 = arg6;
            }

            /*
             *  (non-Javadoc)
             * @see org.openquark.cal.internal.runtime.lecc.RTResultFunction#clearMembers()
             */
            @Override
            public final void clearMembers() {
                arg1 = null;
                arg2 = null;
                arg3 = null;
                arg4 = null;
                arg5 = null;
                arg6 = null;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public final int debug_getNChildren() {
                if (result != null) {
                    return super.debug_getNChildren();
                }
                return 6;
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
                    return arg1;
                case 1:
                    return arg2;
                case 2:
                    return arg3;
                case 3:
                    return arg4;
                case 4:
                    return arg5;
                case 5:
                    return arg6;
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

                if (childN >= 0 && childN < 6) {
                    return " ";
                }

                throw new IndexOutOfBoundsException();
            }

            public static final class _L extends _6 {
                public _L(RTSupercombinator function, RTValue arg1, RTValue arg2, RTValue arg3, RTValue arg4, RTValue arg5, RTValue arg6) {
                        super(function, arg1, arg2, arg3, arg4, arg5, arg6);
                }
                /**
                 * {@inheritDoc}
                 */
                @Override
                protected final RTValue reduce(RTExecutionContext ec)
                        throws CALExecutorException {
                    // Reduce from this application
                    // Update and return result
                    if (arg1 != null) {
                        setResult(function.f6L(
                            RTValue.lastRef(arg1, (arg1 = null)),
                            RTValue.lastRef(arg2, (arg2 = null)),
                            RTValue.lastRef(arg3, (arg3 = null)),
                            RTValue.lastRef(arg4, (arg4 = null)),
                            RTValue.lastRef(arg5, (arg5 = null)),
                            RTValue.lastRef(arg6, (arg6 = null)),
                            ec));
                    } else if (result == null) {
                        throw new NullPointerException(
                                "Invalid reduction state in application.  This is probably caused by a circular function definition.");
                    }
                    return (result);
                }
            }
            public static final class _S extends _6 {
                public _S(RTSupercombinator function, RTValue arg1, RTValue arg2, RTValue arg3, RTValue arg4, RTValue arg5, RTValue arg6) {
                        super(function, arg1, arg2, arg3, arg4, arg5, arg6);
                }
                /**
                 * {@inheritDoc}
                 */
                @Override
                protected final RTValue reduce(RTExecutionContext ec)
                        throws CALExecutorException {
                    // Reduce from this application
                    // Update and return result
                    if (arg1 != null) {
                        setResult(function.f6S(
                            RTValue.lastRef(arg1, (arg1 = null)),
                            RTValue.lastRef(arg2, (arg2 = null)),
                            RTValue.lastRef(arg3, (arg3 = null)),
                            RTValue.lastRef(arg4, (arg4 = null)),
                            RTValue.lastRef(arg5, (arg5 = null)),
                            RTValue.lastRef(arg6, (arg6 = null)),
                            ec));
                    } else if (result == null) {
                        throw new NullPointerException(
                                "Invalid reduction state in application.  This is probably caused by a circular function definition.");
                    }
                    return (result);
                }
            }
        }

        /**
         * WARNING- this class was automatically generated by FullAppNodeGenerator. DO NOT MANUALLY EDIT THIS CLASS.
         *
         * This class is used to represent a full application of an
         * arity 7 function.
         */
        public static abstract class _7 extends General {

            RTValue arg1;
            RTValue arg2;
            RTValue arg3;
            RTValue arg4;
            RTValue arg5;
            RTValue arg6;
            RTValue arg7;

            public _7 (RTSupercombinator function, RTValue arg1, RTValue arg2, RTValue arg3, RTValue arg4, RTValue arg5, RTValue arg6, RTValue arg7) {
                super (function);
                assert (arg1 != null && arg2 != null && arg3 != null && arg4 != null && arg5 != null && arg6 != null && arg7 != null) : "Invalid argument value in RTFullApp.General._7 constructor.";

                this.arg1 = arg1;
                this.arg2 = arg2;
                this.arg3 = arg3;
                this.arg4 = arg4;
                this.arg5 = arg5;
                this.arg6 = arg6;
                this.arg7 = arg7;
            }

            /*
             *  (non-Javadoc)
             * @see org.openquark.cal.internal.runtime.lecc.RTResultFunction#clearMembers()
             */
            @Override
            public final void clearMembers() {
                arg1 = null;
                arg2 = null;
                arg3 = null;
                arg4 = null;
                arg5 = null;
                arg6 = null;
                arg7 = null;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public final int debug_getNChildren() {
                if (result != null) {
                    return super.debug_getNChildren();
                }
                return 7;
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
                    return arg1;
                case 1:
                    return arg2;
                case 2:
                    return arg3;
                case 3:
                    return arg4;
                case 4:
                    return arg5;
                case 5:
                    return arg6;
                case 6:
                    return arg7;
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

                if (childN >= 0 && childN < 7) {
                    return " ";
                }

                throw new IndexOutOfBoundsException();
            }

            public static final class _L extends _7 {
                public _L(RTSupercombinator function, RTValue arg1, RTValue arg2, RTValue arg3, RTValue arg4, RTValue arg5, RTValue arg6, RTValue arg7) {
                        super(function, arg1, arg2, arg3, arg4, arg5, arg6, arg7);
                }
                /**
                 * {@inheritDoc}
                 */
                @Override
                protected final RTValue reduce(RTExecutionContext ec)
                        throws CALExecutorException {
                    // Reduce from this application
                    // Update and return result
                    if (arg1 != null) {
                        setResult(function.f7L(
                            RTValue.lastRef(arg1, (arg1 = null)),
                            RTValue.lastRef(arg2, (arg2 = null)),
                            RTValue.lastRef(arg3, (arg3 = null)),
                            RTValue.lastRef(arg4, (arg4 = null)),
                            RTValue.lastRef(arg5, (arg5 = null)),
                            RTValue.lastRef(arg6, (arg6 = null)),
                            RTValue.lastRef(arg7, (arg7 = null)),
                            ec));
                    } else if (result == null) {
                        throw new NullPointerException(
                                "Invalid reduction state in application.  This is probably caused by a circular function definition.");
                    }
                    return (result);
                }
            }
            public static final class _S extends _7 {
                public _S(RTSupercombinator function, RTValue arg1, RTValue arg2, RTValue arg3, RTValue arg4, RTValue arg5, RTValue arg6, RTValue arg7) {
                        super(function, arg1, arg2, arg3, arg4, arg5, arg6, arg7);
                }
                /**
                 * {@inheritDoc}
                 */
                @Override
                protected final RTValue reduce(RTExecutionContext ec)
                        throws CALExecutorException {
                    // Reduce from this application
                    // Update and return result
                    if (arg1 != null) {
                        setResult(function.f7S(
                            RTValue.lastRef(arg1, (arg1 = null)),
                            RTValue.lastRef(arg2, (arg2 = null)),
                            RTValue.lastRef(arg3, (arg3 = null)),
                            RTValue.lastRef(arg4, (arg4 = null)),
                            RTValue.lastRef(arg5, (arg5 = null)),
                            RTValue.lastRef(arg6, (arg6 = null)),
                            RTValue.lastRef(arg7, (arg7 = null)),
                            ec));
                    } else if (result == null) {
                        throw new NullPointerException(
                                "Invalid reduction state in application.  This is probably caused by a circular function definition.");
                    }
                    return (result);
                }
            }
        }

        /**
         * WARNING- this class was automatically generated by FullAppNodeGenerator. DO NOT MANUALLY EDIT THIS CLASS.
         *
         * This class is used to represent a full application of an
         * arity 8 function.
         */
        public static abstract class _8 extends General {

            RTValue arg1;
            RTValue arg2;
            RTValue arg3;
            RTValue arg4;
            RTValue arg5;
            RTValue arg6;
            RTValue arg7;
            RTValue arg8;

            public _8 (RTSupercombinator function, RTValue arg1, RTValue arg2, RTValue arg3, RTValue arg4, RTValue arg5, RTValue arg6, RTValue arg7, RTValue arg8) {
                super (function);
                assert (arg1 != null && arg2 != null && arg3 != null && arg4 != null && arg5 != null && arg6 != null && arg7 != null && arg8 != null) : "Invalid argument value in RTFullApp.General._8 constructor.";

                this.arg1 = arg1;
                this.arg2 = arg2;
                this.arg3 = arg3;
                this.arg4 = arg4;
                this.arg5 = arg5;
                this.arg6 = arg6;
                this.arg7 = arg7;
                this.arg8 = arg8;
            }

            /*
             *  (non-Javadoc)
             * @see org.openquark.cal.internal.runtime.lecc.RTResultFunction#clearMembers()
             */
            @Override
            public final void clearMembers() {
                arg1 = null;
                arg2 = null;
                arg3 = null;
                arg4 = null;
                arg5 = null;
                arg6 = null;
                arg7 = null;
                arg8 = null;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public final int debug_getNChildren() {
                if (result != null) {
                    return super.debug_getNChildren();
                }
                return 8;
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
                    return arg1;
                case 1:
                    return arg2;
                case 2:
                    return arg3;
                case 3:
                    return arg4;
                case 4:
                    return arg5;
                case 5:
                    return arg6;
                case 6:
                    return arg7;
                case 7:
                    return arg8;
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

                if (childN >= 0 && childN < 8) {
                    return " ";
                }

                throw new IndexOutOfBoundsException();
            }

            public static final class _L extends _8 {
                public _L(RTSupercombinator function, RTValue arg1, RTValue arg2, RTValue arg3, RTValue arg4, RTValue arg5, RTValue arg6, RTValue arg7, RTValue arg8) {
                        super(function, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8);
                }
                /**
                 * {@inheritDoc}
                 */
                @Override
                protected final RTValue reduce(RTExecutionContext ec)
                        throws CALExecutorException {
                    // Reduce from this application
                    // Update and return result
                    if (arg1 != null) {
                        setResult(function.f8L(
                            RTValue.lastRef(arg1, (arg1 = null)),
                            RTValue.lastRef(arg2, (arg2 = null)),
                            RTValue.lastRef(arg3, (arg3 = null)),
                            RTValue.lastRef(arg4, (arg4 = null)),
                            RTValue.lastRef(arg5, (arg5 = null)),
                            RTValue.lastRef(arg6, (arg6 = null)),
                            RTValue.lastRef(arg7, (arg7 = null)),
                            RTValue.lastRef(arg8, (arg8 = null)),
                            ec));
                    } else if (result == null) {
                        throw new NullPointerException(
                                "Invalid reduction state in application.  This is probably caused by a circular function definition.");
                    }
                    return (result);
                }
            }
            public static final class _S extends _8 {
                public _S(RTSupercombinator function, RTValue arg1, RTValue arg2, RTValue arg3, RTValue arg4, RTValue arg5, RTValue arg6, RTValue arg7, RTValue arg8) {
                        super(function, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8);
                }
                /**
                 * {@inheritDoc}
                 */
                @Override
                protected final RTValue reduce(RTExecutionContext ec)
                        throws CALExecutorException {
                    // Reduce from this application
                    // Update and return result
                    if (arg1 != null) {
                        setResult(function.f8S(
                            RTValue.lastRef(arg1, (arg1 = null)),
                            RTValue.lastRef(arg2, (arg2 = null)),
                            RTValue.lastRef(arg3, (arg3 = null)),
                            RTValue.lastRef(arg4, (arg4 = null)),
                            RTValue.lastRef(arg5, (arg5 = null)),
                            RTValue.lastRef(arg6, (arg6 = null)),
                            RTValue.lastRef(arg7, (arg7 = null)),
                            RTValue.lastRef(arg8, (arg8 = null)),
                            ec));
                    } else if (result == null) {
                        throw new NullPointerException(
                                "Invalid reduction state in application.  This is probably caused by a circular function definition.");
                    }
                    return (result);
                }
            }
        }

        /**
         * WARNING- this class was automatically generated by FullAppNodeGenerator. DO NOT MANUALLY EDIT THIS CLASS.
         *
         * This class is used to represent a full application of an
         * arity 9 function.
         */
        public static abstract class _9 extends General {

            RTValue arg1;
            RTValue arg2;
            RTValue arg3;
            RTValue arg4;
            RTValue arg5;
            RTValue arg6;
            RTValue arg7;
            RTValue arg8;
            RTValue arg9;

            public _9 (RTSupercombinator function, RTValue arg1, RTValue arg2, RTValue arg3, RTValue arg4, RTValue arg5, RTValue arg6, RTValue arg7, RTValue arg8, RTValue arg9) {
                super (function);
                assert (arg1 != null && arg2 != null && arg3 != null && arg4 != null && arg5 != null && arg6 != null && arg7 != null && arg8 != null && arg9 != null) : "Invalid argument value in RTFullApp.General._9 constructor.";

                this.arg1 = arg1;
                this.arg2 = arg2;
                this.arg3 = arg3;
                this.arg4 = arg4;
                this.arg5 = arg5;
                this.arg6 = arg6;
                this.arg7 = arg7;
                this.arg8 = arg8;
                this.arg9 = arg9;
            }

            /*
             *  (non-Javadoc)
             * @see org.openquark.cal.internal.runtime.lecc.RTResultFunction#clearMembers()
             */
            @Override
            public final void clearMembers() {
                arg1 = null;
                arg2 = null;
                arg3 = null;
                arg4 = null;
                arg5 = null;
                arg6 = null;
                arg7 = null;
                arg8 = null;
                arg9 = null;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public final int debug_getNChildren() {
                if (result != null) {
                    return super.debug_getNChildren();
                }
                return 9;
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
                    return arg1;
                case 1:
                    return arg2;
                case 2:
                    return arg3;
                case 3:
                    return arg4;
                case 4:
                    return arg5;
                case 5:
                    return arg6;
                case 6:
                    return arg7;
                case 7:
                    return arg8;
                case 8:
                    return arg9;
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

                if (childN >= 0 && childN < 9) {
                    return " ";
                }

                throw new IndexOutOfBoundsException();
            }

            public static final class _L extends _9 {
                public _L(RTSupercombinator function, RTValue arg1, RTValue arg2, RTValue arg3, RTValue arg4, RTValue arg5, RTValue arg6, RTValue arg7, RTValue arg8, RTValue arg9) {
                        super(function, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9);
                }
                /**
                 * {@inheritDoc}
                 */
                @Override
                protected final RTValue reduce(RTExecutionContext ec)
                        throws CALExecutorException {
                    // Reduce from this application
                    // Update and return result
                    if (arg1 != null) {
                        setResult(function.f9L(
                            RTValue.lastRef(arg1, (arg1 = null)),
                            RTValue.lastRef(arg2, (arg2 = null)),
                            RTValue.lastRef(arg3, (arg3 = null)),
                            RTValue.lastRef(arg4, (arg4 = null)),
                            RTValue.lastRef(arg5, (arg5 = null)),
                            RTValue.lastRef(arg6, (arg6 = null)),
                            RTValue.lastRef(arg7, (arg7 = null)),
                            RTValue.lastRef(arg8, (arg8 = null)),
                            RTValue.lastRef(arg9, (arg9 = null)),
                            ec));
                    } else if (result == null) {
                        throw new NullPointerException(
                                "Invalid reduction state in application.  This is probably caused by a circular function definition.");
                    }
                    return (result);
                }
            }
            public static final class _S extends _9 {
                public _S(RTSupercombinator function, RTValue arg1, RTValue arg2, RTValue arg3, RTValue arg4, RTValue arg5, RTValue arg6, RTValue arg7, RTValue arg8, RTValue arg9) {
                        super(function, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9);
                }
                /**
                 * {@inheritDoc}
                 */
                @Override
                protected final RTValue reduce(RTExecutionContext ec)
                        throws CALExecutorException {
                    // Reduce from this application
                    // Update and return result
                    if (arg1 != null) {
                        setResult(function.f9S(
                            RTValue.lastRef(arg1, (arg1 = null)),
                            RTValue.lastRef(arg2, (arg2 = null)),
                            RTValue.lastRef(arg3, (arg3 = null)),
                            RTValue.lastRef(arg4, (arg4 = null)),
                            RTValue.lastRef(arg5, (arg5 = null)),
                            RTValue.lastRef(arg6, (arg6 = null)),
                            RTValue.lastRef(arg7, (arg7 = null)),
                            RTValue.lastRef(arg8, (arg8 = null)),
                            RTValue.lastRef(arg9, (arg9 = null)),
                            ec));
                    } else if (result == null) {
                        throw new NullPointerException(
                                "Invalid reduction state in application.  This is probably caused by a circular function definition.");
                    }
                    return (result);
                }
            }
        }

        /**
         * WARNING- this class was automatically generated by FullAppNodeGenerator. DO NOT MANUALLY EDIT THIS CLASS.
         *
         * This class is used to represent a full application of an
         * arity 10 function.
         */
        public static abstract class _10 extends General {

            RTValue arg1;
            RTValue arg2;
            RTValue arg3;
            RTValue arg4;
            RTValue arg5;
            RTValue arg6;
            RTValue arg7;
            RTValue arg8;
            RTValue arg9;
            RTValue arg10;

            public _10 (RTSupercombinator function, RTValue arg1, RTValue arg2, RTValue arg3, RTValue arg4, RTValue arg5, RTValue arg6, RTValue arg7, RTValue arg8, RTValue arg9, RTValue arg10) {
                super (function);
                assert (arg1 != null && arg2 != null && arg3 != null && arg4 != null && arg5 != null && arg6 != null && arg7 != null && arg8 != null && arg9 != null && arg10 != null) : "Invalid argument value in RTFullApp.General._10 constructor.";

                this.arg1 = arg1;
                this.arg2 = arg2;
                this.arg3 = arg3;
                this.arg4 = arg4;
                this.arg5 = arg5;
                this.arg6 = arg6;
                this.arg7 = arg7;
                this.arg8 = arg8;
                this.arg9 = arg9;
                this.arg10 = arg10;
            }

            /*
             *  (non-Javadoc)
             * @see org.openquark.cal.internal.runtime.lecc.RTResultFunction#clearMembers()
             */
            @Override
            public final void clearMembers() {
                arg1 = null;
                arg2 = null;
                arg3 = null;
                arg4 = null;
                arg5 = null;
                arg6 = null;
                arg7 = null;
                arg8 = null;
                arg9 = null;
                arg10 = null;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public final int debug_getNChildren() {
                if (result != null) {
                    return super.debug_getNChildren();
                }
                return 10;
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
                    return arg1;
                case 1:
                    return arg2;
                case 2:
                    return arg3;
                case 3:
                    return arg4;
                case 4:
                    return arg5;
                case 5:
                    return arg6;
                case 6:
                    return arg7;
                case 7:
                    return arg8;
                case 8:
                    return arg9;
                case 9:
                    return arg10;
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

                if (childN >= 0 && childN < 10) {
                    return " ";
                }

                throw new IndexOutOfBoundsException();
            }

            public static final class _L extends _10 {
                public _L(RTSupercombinator function, RTValue arg1, RTValue arg2, RTValue arg3, RTValue arg4, RTValue arg5, RTValue arg6, RTValue arg7, RTValue arg8, RTValue arg9, RTValue arg10) {
                        super(function, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10);
                }
                /**
                 * {@inheritDoc}
                 */
                @Override
                protected final RTValue reduce(RTExecutionContext ec)
                        throws CALExecutorException {
                    // Reduce from this application
                    // Update and return result
                    if (arg1 != null) {
                        setResult(function.f10L(
                            RTValue.lastRef(arg1, (arg1 = null)),
                            RTValue.lastRef(arg2, (arg2 = null)),
                            RTValue.lastRef(arg3, (arg3 = null)),
                            RTValue.lastRef(arg4, (arg4 = null)),
                            RTValue.lastRef(arg5, (arg5 = null)),
                            RTValue.lastRef(arg6, (arg6 = null)),
                            RTValue.lastRef(arg7, (arg7 = null)),
                            RTValue.lastRef(arg8, (arg8 = null)),
                            RTValue.lastRef(arg9, (arg9 = null)),
                            RTValue.lastRef(arg10, (arg10 = null)),
                            ec));
                    } else if (result == null) {
                        throw new NullPointerException(
                                "Invalid reduction state in application.  This is probably caused by a circular function definition.");
                    }
                    return (result);
                }
            }
            public static final class _S extends _10 {
                public _S(RTSupercombinator function, RTValue arg1, RTValue arg2, RTValue arg3, RTValue arg4, RTValue arg5, RTValue arg6, RTValue arg7, RTValue arg8, RTValue arg9, RTValue arg10) {
                        super(function, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10);
                }
                /**
                 * {@inheritDoc}
                 */
                @Override
                protected final RTValue reduce(RTExecutionContext ec)
                        throws CALExecutorException {
                    // Reduce from this application
                    // Update and return result
                    if (arg1 != null) {
                        setResult(function.f10S(
                            RTValue.lastRef(arg1, (arg1 = null)),
                            RTValue.lastRef(arg2, (arg2 = null)),
                            RTValue.lastRef(arg3, (arg3 = null)),
                            RTValue.lastRef(arg4, (arg4 = null)),
                            RTValue.lastRef(arg5, (arg5 = null)),
                            RTValue.lastRef(arg6, (arg6 = null)),
                            RTValue.lastRef(arg7, (arg7 = null)),
                            RTValue.lastRef(arg8, (arg8 = null)),
                            RTValue.lastRef(arg9, (arg9 = null)),
                            RTValue.lastRef(arg10, (arg10 = null)),
                            ec));
                    } else if (result == null) {
                        throw new NullPointerException(
                                "Invalid reduction state in application.  This is probably caused by a circular function definition.");
                    }
                    return (result);
                }
            }
        }

        /**
         * WARNING- this class was automatically generated by FullAppNodeGenerator. DO NOT MANUALLY EDIT THIS CLASS.
         *
         * This class is used to represent a full application of an
         * arity 11 function.
         */
        public static abstract class _11 extends General {

            RTValue arg1;
            RTValue arg2;
            RTValue arg3;
            RTValue arg4;
            RTValue arg5;
            RTValue arg6;
            RTValue arg7;
            RTValue arg8;
            RTValue arg9;
            RTValue arg10;
            RTValue arg11;

            public _11 (RTSupercombinator function, RTValue arg1, RTValue arg2, RTValue arg3, RTValue arg4, RTValue arg5, RTValue arg6, RTValue arg7, RTValue arg8, RTValue arg9, RTValue arg10, RTValue arg11) {
                super (function);
                assert (arg1 != null && arg2 != null && arg3 != null && arg4 != null && arg5 != null && arg6 != null && arg7 != null && arg8 != null && arg9 != null && arg10 != null && arg11 != null) : "Invalid argument value in RTFullApp.General._11 constructor.";

                this.arg1 = arg1;
                this.arg2 = arg2;
                this.arg3 = arg3;
                this.arg4 = arg4;
                this.arg5 = arg5;
                this.arg6 = arg6;
                this.arg7 = arg7;
                this.arg8 = arg8;
                this.arg9 = arg9;
                this.arg10 = arg10;
                this.arg11 = arg11;
            }

            /*
             *  (non-Javadoc)
             * @see org.openquark.cal.internal.runtime.lecc.RTResultFunction#clearMembers()
             */
            @Override
            public final void clearMembers() {
                arg1 = null;
                arg2 = null;
                arg3 = null;
                arg4 = null;
                arg5 = null;
                arg6 = null;
                arg7 = null;
                arg8 = null;
                arg9 = null;
                arg10 = null;
                arg11 = null;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public final int debug_getNChildren() {
                if (result != null) {
                    return super.debug_getNChildren();
                }
                return 11;
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
                    return arg1;
                case 1:
                    return arg2;
                case 2:
                    return arg3;
                case 3:
                    return arg4;
                case 4:
                    return arg5;
                case 5:
                    return arg6;
                case 6:
                    return arg7;
                case 7:
                    return arg8;
                case 8:
                    return arg9;
                case 9:
                    return arg10;
                case 10:
                    return arg11;
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

                if (childN >= 0 && childN < 11) {
                    return " ";
                }

                throw new IndexOutOfBoundsException();
            }

            public static final class _L extends _11 {
                public _L(RTSupercombinator function, RTValue arg1, RTValue arg2, RTValue arg3, RTValue arg4, RTValue arg5, RTValue arg6, RTValue arg7, RTValue arg8, RTValue arg9, RTValue arg10, RTValue arg11) {
                        super(function, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11);
                }
                /**
                 * {@inheritDoc}
                 */
                @Override
                protected final RTValue reduce(RTExecutionContext ec)
                        throws CALExecutorException {
                    // Reduce from this application
                    // Update and return result
                    if (arg1 != null) {
                        setResult(function.f11L(
                            RTValue.lastRef(arg1, (arg1 = null)),
                            RTValue.lastRef(arg2, (arg2 = null)),
                            RTValue.lastRef(arg3, (arg3 = null)),
                            RTValue.lastRef(arg4, (arg4 = null)),
                            RTValue.lastRef(arg5, (arg5 = null)),
                            RTValue.lastRef(arg6, (arg6 = null)),
                            RTValue.lastRef(arg7, (arg7 = null)),
                            RTValue.lastRef(arg8, (arg8 = null)),
                            RTValue.lastRef(arg9, (arg9 = null)),
                            RTValue.lastRef(arg10, (arg10 = null)),
                            RTValue.lastRef(arg11, (arg11 = null)),
                            ec));
                    } else if (result == null) {
                        throw new NullPointerException(
                                "Invalid reduction state in application.  This is probably caused by a circular function definition.");
                    }
                    return (result);
                }
            }
            public static final class _S extends _11 {
                public _S(RTSupercombinator function, RTValue arg1, RTValue arg2, RTValue arg3, RTValue arg4, RTValue arg5, RTValue arg6, RTValue arg7, RTValue arg8, RTValue arg9, RTValue arg10, RTValue arg11) {
                        super(function, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11);
                }
                /**
                 * {@inheritDoc}
                 */
                @Override
                protected final RTValue reduce(RTExecutionContext ec)
                        throws CALExecutorException {
                    // Reduce from this application
                    // Update and return result
                    if (arg1 != null) {
                        setResult(function.f11S(
                            RTValue.lastRef(arg1, (arg1 = null)),
                            RTValue.lastRef(arg2, (arg2 = null)),
                            RTValue.lastRef(arg3, (arg3 = null)),
                            RTValue.lastRef(arg4, (arg4 = null)),
                            RTValue.lastRef(arg5, (arg5 = null)),
                            RTValue.lastRef(arg6, (arg6 = null)),
                            RTValue.lastRef(arg7, (arg7 = null)),
                            RTValue.lastRef(arg8, (arg8 = null)),
                            RTValue.lastRef(arg9, (arg9 = null)),
                            RTValue.lastRef(arg10, (arg10 = null)),
                            RTValue.lastRef(arg11, (arg11 = null)),
                            ec));
                    } else if (result == null) {
                        throw new NullPointerException(
                                "Invalid reduction state in application.  This is probably caused by a circular function definition.");
                    }
                    return (result);
                }
            }
        }

        /**
         * WARNING- this class was automatically generated by FullAppNodeGenerator. DO NOT MANUALLY EDIT THIS CLASS.
         *
         * This class is used to represent a full application of an
         * arity 12 function.
         */
        public static abstract class _12 extends General {

            RTValue arg1;
            RTValue arg2;
            RTValue arg3;
            RTValue arg4;
            RTValue arg5;
            RTValue arg6;
            RTValue arg7;
            RTValue arg8;
            RTValue arg9;
            RTValue arg10;
            RTValue arg11;
            RTValue arg12;

            public _12 (RTSupercombinator function, RTValue arg1, RTValue arg2, RTValue arg3, RTValue arg4, RTValue arg5, RTValue arg6, RTValue arg7, RTValue arg8, RTValue arg9, RTValue arg10, RTValue arg11, RTValue arg12) {
                super (function);
                assert (arg1 != null && arg2 != null && arg3 != null && arg4 != null && arg5 != null && arg6 != null && arg7 != null && arg8 != null && arg9 != null && arg10 != null && arg11 != null && arg12 != null) : "Invalid argument value in RTFullApp.General._12 constructor.";

                this.arg1 = arg1;
                this.arg2 = arg2;
                this.arg3 = arg3;
                this.arg4 = arg4;
                this.arg5 = arg5;
                this.arg6 = arg6;
                this.arg7 = arg7;
                this.arg8 = arg8;
                this.arg9 = arg9;
                this.arg10 = arg10;
                this.arg11 = arg11;
                this.arg12 = arg12;
            }

            /*
             *  (non-Javadoc)
             * @see org.openquark.cal.internal.runtime.lecc.RTResultFunction#clearMembers()
             */
            @Override
            public final void clearMembers() {
                arg1 = null;
                arg2 = null;
                arg3 = null;
                arg4 = null;
                arg5 = null;
                arg6 = null;
                arg7 = null;
                arg8 = null;
                arg9 = null;
                arg10 = null;
                arg11 = null;
                arg12 = null;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public final int debug_getNChildren() {
                if (result != null) {
                    return super.debug_getNChildren();
                }
                return 12;
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
                    return arg1;
                case 1:
                    return arg2;
                case 2:
                    return arg3;
                case 3:
                    return arg4;
                case 4:
                    return arg5;
                case 5:
                    return arg6;
                case 6:
                    return arg7;
                case 7:
                    return arg8;
                case 8:
                    return arg9;
                case 9:
                    return arg10;
                case 10:
                    return arg11;
                case 11:
                    return arg12;
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

                if (childN >= 0 && childN < 12) {
                    return " ";
                }

                throw new IndexOutOfBoundsException();
            }

            public static final class _L extends _12 {
                public _L(RTSupercombinator function, RTValue arg1, RTValue arg2, RTValue arg3, RTValue arg4, RTValue arg5, RTValue arg6, RTValue arg7, RTValue arg8, RTValue arg9, RTValue arg10, RTValue arg11, RTValue arg12) {
                        super(function, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12);
                }
                /**
                 * {@inheritDoc}
                 */
                @Override
                protected final RTValue reduce(RTExecutionContext ec)
                        throws CALExecutorException {
                    // Reduce from this application
                    // Update and return result
                    if (arg1 != null) {
                        setResult(function.f12L(
                            RTValue.lastRef(arg1, (arg1 = null)),
                            RTValue.lastRef(arg2, (arg2 = null)),
                            RTValue.lastRef(arg3, (arg3 = null)),
                            RTValue.lastRef(arg4, (arg4 = null)),
                            RTValue.lastRef(arg5, (arg5 = null)),
                            RTValue.lastRef(arg6, (arg6 = null)),
                            RTValue.lastRef(arg7, (arg7 = null)),
                            RTValue.lastRef(arg8, (arg8 = null)),
                            RTValue.lastRef(arg9, (arg9 = null)),
                            RTValue.lastRef(arg10, (arg10 = null)),
                            RTValue.lastRef(arg11, (arg11 = null)),
                            RTValue.lastRef(arg12, (arg12 = null)),
                            ec));
                    } else if (result == null) {
                        throw new NullPointerException(
                                "Invalid reduction state in application.  This is probably caused by a circular function definition.");
                    }
                    return (result);
                }
            }
            public static final class _S extends _12 {
                public _S(RTSupercombinator function, RTValue arg1, RTValue arg2, RTValue arg3, RTValue arg4, RTValue arg5, RTValue arg6, RTValue arg7, RTValue arg8, RTValue arg9, RTValue arg10, RTValue arg11, RTValue arg12) {
                        super(function, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12);
                }
                /**
                 * {@inheritDoc}
                 */
                @Override
                protected final RTValue reduce(RTExecutionContext ec)
                        throws CALExecutorException {
                    // Reduce from this application
                    // Update and return result
                    if (arg1 != null) {
                        setResult(function.f12S(
                            RTValue.lastRef(arg1, (arg1 = null)),
                            RTValue.lastRef(arg2, (arg2 = null)),
                            RTValue.lastRef(arg3, (arg3 = null)),
                            RTValue.lastRef(arg4, (arg4 = null)),
                            RTValue.lastRef(arg5, (arg5 = null)),
                            RTValue.lastRef(arg6, (arg6 = null)),
                            RTValue.lastRef(arg7, (arg7 = null)),
                            RTValue.lastRef(arg8, (arg8 = null)),
                            RTValue.lastRef(arg9, (arg9 = null)),
                            RTValue.lastRef(arg10, (arg10 = null)),
                            RTValue.lastRef(arg11, (arg11 = null)),
                            RTValue.lastRef(arg12, (arg12 = null)),
                            ec));
                    } else if (result == null) {
                        throw new NullPointerException(
                                "Invalid reduction state in application.  This is probably caused by a circular function definition.");
                    }
                    return (result);
                }
            }
        }

        /**
         * WARNING- this class was automatically generated by FullAppNodeGenerator. DO NOT MANUALLY EDIT THIS CLASS.
         *
         * This class is used to represent a full application of an
         * arity 13 function.
         */
        public static abstract class _13 extends General {

            RTValue arg1;
            RTValue arg2;
            RTValue arg3;
            RTValue arg4;
            RTValue arg5;
            RTValue arg6;
            RTValue arg7;
            RTValue arg8;
            RTValue arg9;
            RTValue arg10;
            RTValue arg11;
            RTValue arg12;
            RTValue arg13;

            public _13 (RTSupercombinator function, RTValue arg1, RTValue arg2, RTValue arg3, RTValue arg4, RTValue arg5, RTValue arg6, RTValue arg7, RTValue arg8, RTValue arg9, RTValue arg10, RTValue arg11, RTValue arg12, RTValue arg13) {
                super (function);
                assert (arg1 != null && arg2 != null && arg3 != null && arg4 != null && arg5 != null && arg6 != null && arg7 != null && arg8 != null && arg9 != null && arg10 != null && arg11 != null && arg12 != null && arg13 != null) : "Invalid argument value in RTFullApp.General._13 constructor.";

                this.arg1 = arg1;
                this.arg2 = arg2;
                this.arg3 = arg3;
                this.arg4 = arg4;
                this.arg5 = arg5;
                this.arg6 = arg6;
                this.arg7 = arg7;
                this.arg8 = arg8;
                this.arg9 = arg9;
                this.arg10 = arg10;
                this.arg11 = arg11;
                this.arg12 = arg12;
                this.arg13 = arg13;
            }

            /*
             *  (non-Javadoc)
             * @see org.openquark.cal.internal.runtime.lecc.RTResultFunction#clearMembers()
             */
            @Override
            public final void clearMembers() {
                arg1 = null;
                arg2 = null;
                arg3 = null;
                arg4 = null;
                arg5 = null;
                arg6 = null;
                arg7 = null;
                arg8 = null;
                arg9 = null;
                arg10 = null;
                arg11 = null;
                arg12 = null;
                arg13 = null;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public final int debug_getNChildren() {
                if (result != null) {
                    return super.debug_getNChildren();
                }
                return 13;
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
                    return arg1;
                case 1:
                    return arg2;
                case 2:
                    return arg3;
                case 3:
                    return arg4;
                case 4:
                    return arg5;
                case 5:
                    return arg6;
                case 6:
                    return arg7;
                case 7:
                    return arg8;
                case 8:
                    return arg9;
                case 9:
                    return arg10;
                case 10:
                    return arg11;
                case 11:
                    return arg12;
                case 12:
                    return arg13;
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

                if (childN >= 0 && childN < 13) {
                    return " ";
                }

                throw new IndexOutOfBoundsException();
            }

            public static final class _L extends _13 {
                public _L(RTSupercombinator function, RTValue arg1, RTValue arg2, RTValue arg3, RTValue arg4, RTValue arg5, RTValue arg6, RTValue arg7, RTValue arg8, RTValue arg9, RTValue arg10, RTValue arg11, RTValue arg12, RTValue arg13) {
                        super(function, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13);
                }
                /**
                 * {@inheritDoc}
                 */
                @Override
                protected final RTValue reduce(RTExecutionContext ec)
                        throws CALExecutorException {
                    // Reduce from this application
                    // Update and return result
                    if (arg1 != null) {
                        setResult(function.f13L(
                            RTValue.lastRef(arg1, (arg1 = null)),
                            RTValue.lastRef(arg2, (arg2 = null)),
                            RTValue.lastRef(arg3, (arg3 = null)),
                            RTValue.lastRef(arg4, (arg4 = null)),
                            RTValue.lastRef(arg5, (arg5 = null)),
                            RTValue.lastRef(arg6, (arg6 = null)),
                            RTValue.lastRef(arg7, (arg7 = null)),
                            RTValue.lastRef(arg8, (arg8 = null)),
                            RTValue.lastRef(arg9, (arg9 = null)),
                            RTValue.lastRef(arg10, (arg10 = null)),
                            RTValue.lastRef(arg11, (arg11 = null)),
                            RTValue.lastRef(arg12, (arg12 = null)),
                            RTValue.lastRef(arg13, (arg13 = null)),
                            ec));
                    } else if (result == null) {
                        throw new NullPointerException(
                                "Invalid reduction state in application.  This is probably caused by a circular function definition.");
                    }
                    return (result);
                }
            }
            public static final class _S extends _13 {
                public _S(RTSupercombinator function, RTValue arg1, RTValue arg2, RTValue arg3, RTValue arg4, RTValue arg5, RTValue arg6, RTValue arg7, RTValue arg8, RTValue arg9, RTValue arg10, RTValue arg11, RTValue arg12, RTValue arg13) {
                        super(function, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13);
                }
                /**
                 * {@inheritDoc}
                 */
                @Override
                protected final RTValue reduce(RTExecutionContext ec)
                        throws CALExecutorException {
                    // Reduce from this application
                    // Update and return result
                    if (arg1 != null) {
                        setResult(function.f13S(
                            RTValue.lastRef(arg1, (arg1 = null)),
                            RTValue.lastRef(arg2, (arg2 = null)),
                            RTValue.lastRef(arg3, (arg3 = null)),
                            RTValue.lastRef(arg4, (arg4 = null)),
                            RTValue.lastRef(arg5, (arg5 = null)),
                            RTValue.lastRef(arg6, (arg6 = null)),
                            RTValue.lastRef(arg7, (arg7 = null)),
                            RTValue.lastRef(arg8, (arg8 = null)),
                            RTValue.lastRef(arg9, (arg9 = null)),
                            RTValue.lastRef(arg10, (arg10 = null)),
                            RTValue.lastRef(arg11, (arg11 = null)),
                            RTValue.lastRef(arg12, (arg12 = null)),
                            RTValue.lastRef(arg13, (arg13 = null)),
                            ec));
                    } else if (result == null) {
                        throw new NullPointerException(
                                "Invalid reduction state in application.  This is probably caused by a circular function definition.");
                    }
                    return (result);
                }
            }
        }

        /**
         * WARNING- this class was automatically generated by FullAppNodeGenerator. DO NOT MANUALLY EDIT THIS CLASS.
         *
         * This class is used to represent a full application of an
         * arity 14 function.
         */
        public static abstract class _14 extends General {

            RTValue arg1;
            RTValue arg2;
            RTValue arg3;
            RTValue arg4;
            RTValue arg5;
            RTValue arg6;
            RTValue arg7;
            RTValue arg8;
            RTValue arg9;
            RTValue arg10;
            RTValue arg11;
            RTValue arg12;
            RTValue arg13;
            RTValue arg14;

            public _14 (RTSupercombinator function, RTValue arg1, RTValue arg2, RTValue arg3, RTValue arg4, RTValue arg5, RTValue arg6, RTValue arg7, RTValue arg8, RTValue arg9, RTValue arg10, RTValue arg11, RTValue arg12, RTValue arg13, RTValue arg14) {
                super (function);
                assert (arg1 != null && arg2 != null && arg3 != null && arg4 != null && arg5 != null && arg6 != null && arg7 != null && arg8 != null && arg9 != null && arg10 != null && arg11 != null && arg12 != null && arg13 != null && arg14 != null) : "Invalid argument value in RTFullApp.General._14 constructor.";

                this.arg1 = arg1;
                this.arg2 = arg2;
                this.arg3 = arg3;
                this.arg4 = arg4;
                this.arg5 = arg5;
                this.arg6 = arg6;
                this.arg7 = arg7;
                this.arg8 = arg8;
                this.arg9 = arg9;
                this.arg10 = arg10;
                this.arg11 = arg11;
                this.arg12 = arg12;
                this.arg13 = arg13;
                this.arg14 = arg14;
            }

            /*
             *  (non-Javadoc)
             * @see org.openquark.cal.internal.runtime.lecc.RTResultFunction#clearMembers()
             */
            @Override
            public final void clearMembers() {
                arg1 = null;
                arg2 = null;
                arg3 = null;
                arg4 = null;
                arg5 = null;
                arg6 = null;
                arg7 = null;
                arg8 = null;
                arg9 = null;
                arg10 = null;
                arg11 = null;
                arg12 = null;
                arg13 = null;
                arg14 = null;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public final int debug_getNChildren() {
                if (result != null) {
                    return super.debug_getNChildren();
                }
                return 14;
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
                    return arg1;
                case 1:
                    return arg2;
                case 2:
                    return arg3;
                case 3:
                    return arg4;
                case 4:
                    return arg5;
                case 5:
                    return arg6;
                case 6:
                    return arg7;
                case 7:
                    return arg8;
                case 8:
                    return arg9;
                case 9:
                    return arg10;
                case 10:
                    return arg11;
                case 11:
                    return arg12;
                case 12:
                    return arg13;
                case 13:
                    return arg14;
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

                if (childN >= 0 && childN < 14) {
                    return " ";
                }

                throw new IndexOutOfBoundsException();
            }

            public static final class _L extends _14 {
                public _L(RTSupercombinator function, RTValue arg1, RTValue arg2, RTValue arg3, RTValue arg4, RTValue arg5, RTValue arg6, RTValue arg7, RTValue arg8, RTValue arg9, RTValue arg10, RTValue arg11, RTValue arg12, RTValue arg13, RTValue arg14) {
                        super(function, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14);
                }
                /**
                 * {@inheritDoc}
                 */
                @Override
                protected final RTValue reduce(RTExecutionContext ec)
                        throws CALExecutorException {
                    // Reduce from this application
                    // Update and return result
                    if (arg1 != null) {
                        setResult(function.f14L(
                            RTValue.lastRef(arg1, (arg1 = null)),
                            RTValue.lastRef(arg2, (arg2 = null)),
                            RTValue.lastRef(arg3, (arg3 = null)),
                            RTValue.lastRef(arg4, (arg4 = null)),
                            RTValue.lastRef(arg5, (arg5 = null)),
                            RTValue.lastRef(arg6, (arg6 = null)),
                            RTValue.lastRef(arg7, (arg7 = null)),
                            RTValue.lastRef(arg8, (arg8 = null)),
                            RTValue.lastRef(arg9, (arg9 = null)),
                            RTValue.lastRef(arg10, (arg10 = null)),
                            RTValue.lastRef(arg11, (arg11 = null)),
                            RTValue.lastRef(arg12, (arg12 = null)),
                            RTValue.lastRef(arg13, (arg13 = null)),
                            RTValue.lastRef(arg14, (arg14 = null)),
                            ec));
                    } else if (result == null) {
                        throw new NullPointerException(
                                "Invalid reduction state in application.  This is probably caused by a circular function definition.");
                    }
                    return (result);
                }
            }
            public static final class _S extends _14 {
                public _S(RTSupercombinator function, RTValue arg1, RTValue arg2, RTValue arg3, RTValue arg4, RTValue arg5, RTValue arg6, RTValue arg7, RTValue arg8, RTValue arg9, RTValue arg10, RTValue arg11, RTValue arg12, RTValue arg13, RTValue arg14) {
                        super(function, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14);
                }
                /**
                 * {@inheritDoc}
                 */
                @Override
                protected final RTValue reduce(RTExecutionContext ec)
                        throws CALExecutorException {
                    // Reduce from this application
                    // Update and return result
                    if (arg1 != null) {
                        setResult(function.f14S(
                            RTValue.lastRef(arg1, (arg1 = null)),
                            RTValue.lastRef(arg2, (arg2 = null)),
                            RTValue.lastRef(arg3, (arg3 = null)),
                            RTValue.lastRef(arg4, (arg4 = null)),
                            RTValue.lastRef(arg5, (arg5 = null)),
                            RTValue.lastRef(arg6, (arg6 = null)),
                            RTValue.lastRef(arg7, (arg7 = null)),
                            RTValue.lastRef(arg8, (arg8 = null)),
                            RTValue.lastRef(arg9, (arg9 = null)),
                            RTValue.lastRef(arg10, (arg10 = null)),
                            RTValue.lastRef(arg11, (arg11 = null)),
                            RTValue.lastRef(arg12, (arg12 = null)),
                            RTValue.lastRef(arg13, (arg13 = null)),
                            RTValue.lastRef(arg14, (arg14 = null)),
                            ec));
                    } else if (result == null) {
                        throw new NullPointerException(
                                "Invalid reduction state in application.  This is probably caused by a circular function definition.");
                    }
                    return (result);
                }
            }
        }

        /**
         * WARNING- this class was automatically generated by FullAppNodeGenerator. DO NOT MANUALLY EDIT THIS CLASS.
         *
         * This class is used to represent a full application of an
         * arity 15 function.
         */
        public static abstract class _15 extends General {

            RTValue arg1;
            RTValue arg2;
            RTValue arg3;
            RTValue arg4;
            RTValue arg5;
            RTValue arg6;
            RTValue arg7;
            RTValue arg8;
            RTValue arg9;
            RTValue arg10;
            RTValue arg11;
            RTValue arg12;
            RTValue arg13;
            RTValue arg14;
            RTValue arg15;

            public _15 (RTSupercombinator function, RTValue arg1, RTValue arg2, RTValue arg3, RTValue arg4, RTValue arg5, RTValue arg6, RTValue arg7, RTValue arg8, RTValue arg9, RTValue arg10, RTValue arg11, RTValue arg12, RTValue arg13, RTValue arg14, RTValue arg15) {
                super (function);
                assert (arg1 != null && arg2 != null && arg3 != null && arg4 != null && arg5 != null && arg6 != null && arg7 != null && arg8 != null && arg9 != null && arg10 != null && arg11 != null && arg12 != null && arg13 != null && arg14 != null && arg15 != null) : "Invalid argument value in RTFullApp.General._15 constructor.";

                this.arg1 = arg1;
                this.arg2 = arg2;
                this.arg3 = arg3;
                this.arg4 = arg4;
                this.arg5 = arg5;
                this.arg6 = arg6;
                this.arg7 = arg7;
                this.arg8 = arg8;
                this.arg9 = arg9;
                this.arg10 = arg10;
                this.arg11 = arg11;
                this.arg12 = arg12;
                this.arg13 = arg13;
                this.arg14 = arg14;
                this.arg15 = arg15;
            }

            /*
             *  (non-Javadoc)
             * @see org.openquark.cal.internal.runtime.lecc.RTResultFunction#clearMembers()
             */
            @Override
            public final void clearMembers() {
                arg1 = null;
                arg2 = null;
                arg3 = null;
                arg4 = null;
                arg5 = null;
                arg6 = null;
                arg7 = null;
                arg8 = null;
                arg9 = null;
                arg10 = null;
                arg11 = null;
                arg12 = null;
                arg13 = null;
                arg14 = null;
                arg15 = null;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public final int debug_getNChildren() {
                if (result != null) {
                    return super.debug_getNChildren();
                }
                return 15;
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
                    return arg1;
                case 1:
                    return arg2;
                case 2:
                    return arg3;
                case 3:
                    return arg4;
                case 4:
                    return arg5;
                case 5:
                    return arg6;
                case 6:
                    return arg7;
                case 7:
                    return arg8;
                case 8:
                    return arg9;
                case 9:
                    return arg10;
                case 10:
                    return arg11;
                case 11:
                    return arg12;
                case 12:
                    return arg13;
                case 13:
                    return arg14;
                case 14:
                    return arg15;
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

                if (childN >= 0 && childN < 15) {
                    return " ";
                }

                throw new IndexOutOfBoundsException();
            }

            public static final class _L extends _15 {
                public _L(RTSupercombinator function, RTValue arg1, RTValue arg2, RTValue arg3, RTValue arg4, RTValue arg5, RTValue arg6, RTValue arg7, RTValue arg8, RTValue arg9, RTValue arg10, RTValue arg11, RTValue arg12, RTValue arg13, RTValue arg14, RTValue arg15) {
                        super(function, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15);
                }
                /**
                 * {@inheritDoc}
                 */
                @Override
                protected final RTValue reduce(RTExecutionContext ec)
                        throws CALExecutorException {
                    // Reduce from this application
                    // Update and return result
                    if (arg1 != null) {
                        setResult(function.f15L(
                            RTValue.lastRef(arg1, (arg1 = null)),
                            RTValue.lastRef(arg2, (arg2 = null)),
                            RTValue.lastRef(arg3, (arg3 = null)),
                            RTValue.lastRef(arg4, (arg4 = null)),
                            RTValue.lastRef(arg5, (arg5 = null)),
                            RTValue.lastRef(arg6, (arg6 = null)),
                            RTValue.lastRef(arg7, (arg7 = null)),
                            RTValue.lastRef(arg8, (arg8 = null)),
                            RTValue.lastRef(arg9, (arg9 = null)),
                            RTValue.lastRef(arg10, (arg10 = null)),
                            RTValue.lastRef(arg11, (arg11 = null)),
                            RTValue.lastRef(arg12, (arg12 = null)),
                            RTValue.lastRef(arg13, (arg13 = null)),
                            RTValue.lastRef(arg14, (arg14 = null)),
                            RTValue.lastRef(arg15, (arg15 = null)),
                            ec));
                    } else if (result == null) {
                        throw new NullPointerException(
                                "Invalid reduction state in application.  This is probably caused by a circular function definition.");
                    }
                    return (result);
                }
            }
            public static final class _S extends _15 {
                public _S(RTSupercombinator function, RTValue arg1, RTValue arg2, RTValue arg3, RTValue arg4, RTValue arg5, RTValue arg6, RTValue arg7, RTValue arg8, RTValue arg9, RTValue arg10, RTValue arg11, RTValue arg12, RTValue arg13, RTValue arg14, RTValue arg15) {
                        super(function, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15);
                }
                /**
                 * {@inheritDoc}
                 */
                @Override
                protected final RTValue reduce(RTExecutionContext ec)
                        throws CALExecutorException {
                    // Reduce from this application
                    // Update and return result
                    if (arg1 != null) {
                        setResult(function.f15S(
                            RTValue.lastRef(arg1, (arg1 = null)),
                            RTValue.lastRef(arg2, (arg2 = null)),
                            RTValue.lastRef(arg3, (arg3 = null)),
                            RTValue.lastRef(arg4, (arg4 = null)),
                            RTValue.lastRef(arg5, (arg5 = null)),
                            RTValue.lastRef(arg6, (arg6 = null)),
                            RTValue.lastRef(arg7, (arg7 = null)),
                            RTValue.lastRef(arg8, (arg8 = null)),
                            RTValue.lastRef(arg9, (arg9 = null)),
                            RTValue.lastRef(arg10, (arg10 = null)),
                            RTValue.lastRef(arg11, (arg11 = null)),
                            RTValue.lastRef(arg12, (arg12 = null)),
                            RTValue.lastRef(arg13, (arg13 = null)),
                            RTValue.lastRef(arg14, (arg14 = null)),
                            RTValue.lastRef(arg15, (arg15 = null)),
                            ec));
                    } else if (result == null) {
                        throw new NullPointerException(
                                "Invalid reduction state in application.  This is probably caused by a circular function definition.");
                    }
                    return (result);
                }
            }
        }







    }
}
