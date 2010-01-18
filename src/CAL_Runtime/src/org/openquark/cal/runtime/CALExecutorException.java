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
 * CALExecutorException.java
 * Creation date: Sept 21, 2006
 * By: Bo Ilic
 */

package org.openquark.cal.runtime;


/**
 * Base class for exceptions involved in evaluating CAL code.
 * <p>  
 * The exception hierarchy is divided into two main branches, one for external exceptions and 
 * one for internal exceptions.
 * <p>
 * Internal exceptions are errors in the implementation of CAL, or errors in setting up the CAL services
 * calls to activate the run-time i.e. they are not caused by the correct operation of the running CAL code.
 * <p>
 * External exceptions are errors in a CAL program.  External errors can originate in several 
 * ways (currently).
 * <ol> 
 *    <li> A call to the CAL Prelude.error function. 
 *    <li> A foreign function an exception.
 *    <li> An arithmetic exception, such as divide by zero, occurs in evaluating the CAL code.
 *    <li> A pattern matching failure, such as (case Just 10.0 of Nothing -> "ok";) or (Left 2.0 :: Either Double Double).Right.value
 * </ol>
 */     
public abstract class CALExecutorException extends Exception {
     
    /** Provides info about where an error appeared in the source file. may be null. */
    private final ErrorInfo errorInfo;

    /**
     * Construct an exception with the given message and associate it with the given
     * Executor and originating exception.
     * @param message String        
     */        
    public CALExecutorException(String message) {
        // Just instantiate the super constructor
        super(message); 
        this.errorInfo = null; 
    }

    /**
     * Construct an exception with the given message and associate it with the given
     * Executor and originating exception.
     * @param message String        
     * @param cause Throwable
     */        
    public CALExecutorException(String message, Throwable cause) {
        // Just instantiate the super constructor
        super(message, cause);
        this.errorInfo = null;
    }

    /**
     * Construct an exception with the given message and associate it with the given
     * Executor and originating exception.
     * @param errorInfo Information that associates the error with the source code. Maybe null.
     * @param message String       
     * @param cause Throwable
     */        
    public CALExecutorException(ErrorInfo errorInfo, String message, Throwable cause) {
        // Just instantiate the super constructor
        super(message, cause);
       
        this.errorInfo = errorInfo;
    }

    /**
     * @return Provides information about where the error call appears in the 
     * source file. This may be null.
     */
    
    public ErrorInfo getErrorInfo(){
        return errorInfo;
    }
    
    /** 
     * Return true if this is an external exception (i.e. an error was generated
     * by the CAL source program)
     * @return boolean
     */
    public abstract boolean isExternal ();
    
    /**
     * Retrieve the message associated with this exception.
     * The chain of originating exceptions is walked and their
     * messages are included.
     * @return String
     */
    @Override
    public String getMessage() {

        StringBuilder sb = new StringBuilder ();
        String sm = super.getMessage();
        if (errorInfo != null){
            sm = errorInfo + ": " + sm;
        }
        if (sm!= null && !sm.equals("")) {
            sb.append (sm);
        }

        // Add the info from the originating exception             
        if (getCause() != null) {
            sb.append("\n"); 
            Throwable cause = getCause();
            String oem = cause.getMessage();
            sb.append ("Caused by: " + cause.toString() + ",  Detail: " + oem + "\n");
        }

        return sb.toString ();
    }  
    
    /**
     * Get the execution result status.
     *   Call this to determine the result status when the execution has been terminated.
     * Note: the runtime must still be available.
     * 
     * @return the type of the exception
     */
    public final CALExecutorException.Type getExceptionType() {

        if (this.isExternal()) {
            // An error occurred in the CAL program.
            if (this instanceof CALExecutorException.ExternalException.ForeignOrPrimitiveFunctionException) {
                return Type.FOREIGN_OR_PRIMITIVE_FUNCTION_EXCEPTION;
                
            } else if (this instanceof CALExecutorException.ExternalException.PrimThrowFunctionException){
                return Type.PRIM_THROW_FUNCTION_CALLED;
                
            } else if (this instanceof CALExecutorException.ExternalException.TerminatedByClientException) {
                return Type.USER_TERMINATED;  
                
            }else  if (this instanceof CALExecutorException.ExternalException.ErrorFunctionException){
                return Type.ERROR_FUNCTION_CALL;                    
           
            }else if (this instanceof CALExecutorException.ExternalException.PatternMatchFailure){
                return Type.PATTERN_MATCH_FAILURE;
            }
            
        } else if (this instanceof CALExecutorException.InternalException){
            return Type.INTERNAL_RUNTIME_ERROR;
        }
        
        throw new IllegalStateException("unrecognized CALExecutorException.Type");
    }
    
    /**
     * Enum for the different types of exceptions.
     * @author Raymond Cypher
     */
    public static final class Type {
        private final String typeString;

        private Type (String s) {
            typeString = s;
        }
        
        @Override
        public String toString() {
            return typeString;
        }

        /** An error occurred during the evaluation of the gem */
        public static final CALExecutorException.Type PRIM_THROW_FUNCTION_CALLED = new Type("PRIM_THROW_FUNCTION_CALLED");
        
        /** An error occurred during a foreign function call. */
        public static final CALExecutorException.Type FOREIGN_OR_PRIMITIVE_FUNCTION_EXCEPTION = new Type("FOREIGN_OR_PRIMITIVE_FUNCTION_EXCEPTION");
        
        /** There was a call to the CAL Prelude.error function. */
        public static final CALExecutorException.Type ERROR_FUNCTION_CALL = new Type("ERROR_FUNCTION_CALL");
        
        /**
         * There was a pattern matching failure (e.g. the evaluated data constructor did not match one of the
         * supplied case patterns in a case expression).
         */
        public static final CALExecutorException.Type PATTERN_MATCH_FAILURE = new Type("PATTERN_MATCH_FAILURE");
        
        /** There was an internal runtime error. */
        public static final CALExecutorException.Type INTERNAL_RUNTIME_ERROR = new Type("INTERNAL_RUNTIME_ERROR");
        
        /** The user terminated. */
        public static final CALExecutorException.Type USER_TERMINATED = new Type("USER_TERMINATED");
      
    }

    /**
      * Base class for Internal exceptions.
      * Currently internal exceptions are always terminating exceptions.
      */
     public static class InternalException extends CALExecutorException {
    
        /**
         * An exception used to deal with VM errors. (e.g. out of memory, stack overflow, etc.)
         * In these situations we will actually pre-allocate the CALVMException member as
         * it may not be safe to allocate a new exception object when the exception actually occurs.
         * Creation date: (2/06/02 2:50:26 PM)
         * @author Ray Cypher
         */
        public static final class VMException extends InternalException {
                   
            private static final long serialVersionUID = 3728243376204270139L;
        
            private VMException(String message) {
                // Just instantiate the super constructor
                super(message);
            }
            
            public static VMException makeInitial () {
                return new VMException("The java virtual machine encountered an error.");
            }
        }

        private static final long serialVersionUID = 1149155136296227623L;
    
        public InternalException (String message) {
            super (message);
        }
          
        public InternalException (String message, Throwable cause ) {
            super (message, cause);
        }
        
        public InternalException (ErrorInfo errorInfo, String message, Throwable cause ) {
            super (errorInfo, message, cause);
        }
        
        /** {@inheritDoc}*/
        @Override
        public boolean isExternal () {
            return false;
        }
     }

    /**
     * A base class for external exceptions.
     * All programmatic exceptions are types of CALProgramException.
     * <p>
     * External errors can originate via:
     * <ol>
     * <li>A call to the CAL Prelude.error function.  
     * <li>A foreign function generates an exception.  
     * <li>An arithmetic exception, such as divide by zero, occurs in evaluating
     * the CAL code.  
     * <li>A pattern matching failure e.g. given the definition,
     *     <pre>
     *     f x = case x of LT -> "LT data cons";;
     *     </pre>
     *     then f GT will produce a pattern match exception.
     * </ol>      
     * A program exception can be either non-terminating (a call to the 'Error' function)
     * or terminating.     
     */
    public static abstract class ExternalException extends CALExecutorException {
        
        /**
         * ErrorException is thrown by the CAL Prelude.error function.  
         */
        public static final class ErrorFunctionException extends ExternalException {
        
            private static final long serialVersionUID = -9169002572980306056L;
        
            public ErrorFunctionException(String message) {
                // Just instantiate the super constructor
                super(message, null);
            }
        
            public ErrorFunctionException(String message, Throwable cause) {
                // Just instantiate the super constructor
                super(message, cause);
            }
            
            public ErrorFunctionException(ErrorInfo errorInfo, String message, Throwable cause) {
                // Just instantiate the super constructor
                super(errorInfo, message, cause);
            }
            
            public ErrorFunctionException(ErrorInfo errorInfo, String message) {
                // Just instantiate the super constructor
                super(errorInfo, message, null);
            }
        }

        /**
         * ForeignOrPrimitiveFunctionException is a wrapper to catch exceptions thrown by foreign or primitive functions 
         * (such as Prelude.divideInt for division by zero). It is not used by Prelude.error and Prelude.primThrow which
         * are handled separately.
         * 
         * @author Ray Cypher
         */
        public static class ForeignOrPrimitiveFunctionException extends ExternalException {
            
            private static final long serialVersionUID = 788290508015376784L;
        
            public ForeignOrPrimitiveFunctionException(String message, Throwable cause) {
                // Just instantiate the super constructor
                super(message, cause);
            }
        }

        /**
         * CALPatternMatchException is thrown for pattern matching failures.  
         *  e.g. given the definition,
         *    f x = case x of LT -> "LT data cons";;
         *    then f GT will produce a pattern match exception.
         * @author Bo Ilic
         */
        public static final class PatternMatchFailure extends ExternalException {
        
            private static final long serialVersionUID = -7497118868782104464L;
        
            public PatternMatchFailure(ErrorInfo errorInfo, String message, Exception cause) {
                // Just instantiate the super constructor
                super(errorInfo, message, cause);
            }
        }

        /**
         * PrimThrowFunctionException is thrown by the Exception.primThrow function used
         * to implement exception handling in CAL code. 
         * @author Bo Ilic
         */
        public static final class PrimThrowFunctionException extends ExternalException {
        
            private static final long serialVersionUID = -7497118868782104464L;
        
            public PrimThrowFunctionException(String message, Throwable cause) {
                // Just instantiate the super constructor
                super(message, cause);
            }
        }

        /**
         * CALForeignFunctionException is thrown in response to a client (e.g. user) terminating execution.
         * @author Ray Cypher
         */
        public static final class TerminatedByClientException extends ExternalException {
            
            private static final long serialVersionUID = 6893267285936825240L;
        
            public TerminatedByClientException (String message, Throwable cause) {
                // Just instantiate the super constructor
                super(message, cause);
            }
        }

        public ExternalException(String message, Throwable cause) {
            // Just instantiate the super constructor
            super(message, cause);
        }
    
        public ExternalException(ErrorInfo errorInfo, String message, Throwable cause) {
            // Just instantiate the super constructor
            super(errorInfo, message, cause);
        }
    
        /** {@inheritDoc}*/
        @Override
        public final boolean isExternal () {
            return true;
        }
    } 
    
}