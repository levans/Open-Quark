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
 * RTValue.java
 * Created: Jan 25, 2002 at 2:53:21 PM
 * By: RCypher
 */
package org.openquark.cal.internal.runtime.lecc;

import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;

import org.openquark.cal.internal.runtime.lecc.RTData.CAL_Opaque;
import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.runtime.CalValue;
import org.openquark.cal.runtime.ErrorInfo;
import org.openquark.cal.runtime.MachineType;


/**
 * RTValue represents the base type of any lecc runtime value (RTSupercombinator and RTData
 * are subclasses).
 * <p>
 * Creation: Nov 11, 2002
 * @author LEvans
 */
public abstract class RTValue extends CalValue {

    private static final AtomicInteger nApplicationCalls = new AtomicInteger(0);

    /** A public instance of InterruptException. */
    public static final CALExecutorException INTERRUPT_EXCEPTION =
        new CALExecutorException.ExternalException.TerminatedByClientException("Evaluation halted by client", null);
    
    public static final void resetNApplicationCalls() {
        nApplicationCalls.set(0);
    }

    public static final int getNApplicationCalls() {
        return nApplicationCalls.get();
    }
    
    public static final void incrementNApplicationCalls() {
        nApplicationCalls.incrementAndGet();
    }

    /**
     * Return the evaluated RTValue.  This may cause the value to be evaluated
     * if this hasn't already been done.
     * @param ec
     * @return RTValue the result of evaluating this RTValue
     * @throws CALExecutorException
     */
    public RTValue evaluate(RTExecutionContext ec) throws CALExecutorException {
        // Base class implementation simply returns 'this'.
        return this;
    }

    /**
     * Return an application of deepSeq to sub parts.
     * @param deepSeq
     * @param rhs
     * @return an application of deepSeq to sub parts
     * @throws CALExecutorException
     */
    public RTValue buildDeepSeq(RTSupercombinator deepSeq, RTValue rhs) throws CALExecutorException {
        // Base implementation does nothing.
        return rhs;
    }

    /**
     * Get the value without forcing evaluation.
     * This method is expected to follow any indirection chains
     * until a leaf node is encountered.
     * @return RTValue the value
     */
    public RTValue getValue() {
        return this;
    }

    /**
     * Get the arity of this value (number of arguments required for full
     * saturation).
     * This will be overridden by RTSupercombinator derived classes.
     * Non-functional values have an arity of 0.  RTFunction's derived
     * classes override this as appropriate to report a specific function's
     * arity.
     * This function (and any overridden version) is expected to return
     * the arity for the RTValue (or sub class) instance
     * on which it is invoked.  It should not look at any other nodes in the
     * graph.
     * @return int the arity
     */
    public int getArity() {
        // Simple values have an arity of 0
        return 0;
    }

    /**
     * Determine if this value should represent a logical 'true' if employed in
     * a logical expression (particularly the condition of an 'if').
     * @return boolean
     */
    public boolean isLogicalTrue() {
        // All normal values are assumed to represent 'true'
        // The one special value 'CAL_Boolean.CAL_False' in the kernel overrides this
        // to 'false'
        return true;
    }

    /**
     * Reduce this RTValue.
     * @param ec
     * @return RTValue the unwound RTValue (through reduction)
     * @throws CALExecutorException
     */
    protected abstract RTValue reduce(RTExecutionContext ec) throws CALExecutorException;

    /**
     * Note the default implementation is unsynchronized! It is overridden to synchronize calls to
     * reduce where appropriate.
     */
    protected RTValue synchronizedReduce(RTExecutionContext ec) throws CALExecutorException {
        return reduce(ec);
    }

    /**
     * Apply this RTValue to an argument (value)
     * @param argument the RTValue this is applied to
     * @return RTValue the applied value
     */
    public RTValue apply(RTValue argument) {
        throw new UnsupportedOperationException("Cannot apply a value to type: " + getClass().getName());
    }


    /**
     * Apply this RTValue to two values
     * @param arg1
     * @param arg2
     * @return the root of the new application graph.
     */
    public RTValue apply(RTValue arg1, RTValue arg2) {
        throw new UnsupportedOperationException("Apply can't be called for a primitive type: " + this.getClass().getName());
    }

    /**
     * Apply this RTValue to three values
     * @param arg1
     * @param arg2
     * @param arg3
     * @return the root of the new application graph.
     */
    public RTValue apply(RTValue arg1, RTValue arg2, RTValue arg3) {
        throw new UnsupportedOperationException("Apply can't be called for a primitive type: " + this.getClass().getName());
    }

    /**
     * Apply this RTValue to four values
     * @param arg1
     * @param arg2
     * @param arg3
     * @param arg4
     * @return the root of the new application graph.
     */
    public RTValue apply(RTValue arg1, RTValue arg2, RTValue arg3, RTValue arg4) {
        throw new UnsupportedOperationException("Apply can't be called for a primitive type: " + this.getClass().getName());
    }


    /**
     * Return the previous argument (until we reach the root).
     * This function follows the graph to the left until it reaches
     * a node which is not an indirection or which has no left hand size.
     * @return RTValue the previous argument
     */
    public RTValue prevArg() {
        // Normal functions are not argument chains (have no previous argument)
        return null;
    }


    /**
     * Obtain the number of argument nodes connected to this graph node.
     * This will be overridden by the general purpose application node.
     * @return int the number of arguments
     */
    public int getArgCount() {
        return 0;
    }

    /**
     * The method returns the argument value i.e. right hand branch, or
     * a general application node.
     * @return the right hand branch as appropriate
     */
    public RTValue getArgValue () {
        throw new UnsupportedOperationException ("Attempt to call getArgValue() on: " + getClass().getName());
    }


    /**
     * Indicate that we have a bad value by throwing an exception
     * @param exceptionMessage the exception message
     * @return RTValue NEVER RETURNED.  This method 'returns' an RTValue in order to be usable in
     * RTValue expressions
     */
    static public RTValue badValue(String exceptionMessage) {
        return badValue(null, exceptionMessage);
    }

    /**
     * Indicate that we have a bad value by throwing an exception
     * @param errorInfo
     * @param exceptionMessage the exception message
     * @return RTValue NEVER RETURNED.  This method 'returns' an RTValue in order to be usable in
     * RTValue expressions
     */
    static public RTValue badValue(ErrorInfo errorInfo, String exceptionMessage) {
        RuntimeException e = new RTValue.InternalException(errorInfo, exceptionMessage);
        if (System.getProperty(LECCMachineConfiguration.DEBUG_INFO_PROP) != null) {
            e.printStackTrace();
        }
        throw e;
    }

    /**
     * Indicate that we have a bad value by throwing an exception.
     * This is used in generated methods that return a boolean.
     * @param errorInfo
     * @param exceptionMessage the exception message
     * @return boolean NEVER RETURNED.  This method 'returns' an boolean in order to be usable in
     * boolean expressions
     */
    static public boolean badValue_boolean (ErrorInfo errorInfo, String exceptionMessage) {
        RuntimeException e = new RTValue.InternalException(errorInfo, exceptionMessage);
        if (System.getProperty(LECCMachineConfiguration.DEBUG_INFO_PROP) != null) {
            e.printStackTrace();
        }
        throw e;
    }

    /**
     * Indicate that we have a bad value by throwing an exception.
     * This is used in generated methods that return a byte.
     * @param errorInfo
     * @param exceptionMessage the exception message
     * @return byte NEVER RETURNED.  This method 'returns' an byte in order to be usable in
     * byte expressions
     */
    static public byte badValue_byte (ErrorInfo errorInfo, String exceptionMessage) {
        RuntimeException e = new RTValue.InternalException(errorInfo, exceptionMessage);
        if (System.getProperty(LECCMachineConfiguration.DEBUG_INFO_PROP) != null) {
            e.printStackTrace();
        }
        throw e;
    }

    /**
     * Indicate that we have a bad value by throwing an exception.
     * This is used in generated methods that return a char.
     * @param errorInfo
     * @param exceptionMessage the exception message
     * @return char NEVER RETURNED.  This method 'returns' an char in order to be usable in
     * char expressions
     */
    static public char badValue_char (ErrorInfo errorInfo, String exceptionMessage) {
        RuntimeException e = new RTValue.InternalException(errorInfo, exceptionMessage);
        if (System.getProperty(LECCMachineConfiguration.DEBUG_INFO_PROP) != null) {
            e.printStackTrace();
        }
        throw e;
    }

    /**
     * Indicate that we have a bad value by throwing an exception.
     * This is used in generated methods that return a double.
     * @param errorInfo
     * @param exceptionMessage the exception message
     * @return double NEVER RETURNED.  This method 'returns' an double in order to be usable in
     * double expressions
     */
    static public double badValue_double (ErrorInfo errorInfo, String exceptionMessage) {
        RuntimeException e = new RTValue.InternalException(errorInfo, exceptionMessage);
        if (System.getProperty(LECCMachineConfiguration.DEBUG_INFO_PROP) != null) {
            e.printStackTrace();
        }
        throw e;
    }

    /**
     * Indicate that we have a bad value by throwing an exception.
     * This is used in generated methods that return a float.
     * @param errorInfo
     * @param exceptionMessage the exception message
     * @return float NEVER RETURNED.  This method 'returns' an float in order to be usable in
     * float expressions
     */
    static public float badValue_float (ErrorInfo errorInfo, String exceptionMessage) {
        RuntimeException e = new RTValue.InternalException(errorInfo, exceptionMessage);
        if (System.getProperty(LECCMachineConfiguration.DEBUG_INFO_PROP) != null) {
            e.printStackTrace();
        }
        throw e;
    }

    /**
     * Indicate that we have a bad value by throwing an exception.
     * This is used in generated methods that return a int.
     * @param errorInfo
     * @param exceptionMessage the exception message
     * @return int NEVER RETURNED.  This method 'returns' an int in order to be usable in
     * int expressions
     */
    static public int badValue_int (ErrorInfo errorInfo, String exceptionMessage) {
        RuntimeException e = new RTValue.InternalException(errorInfo, exceptionMessage);
        if (System.getProperty(LECCMachineConfiguration.DEBUG_INFO_PROP) != null) {
            e.printStackTrace();
        }
        throw e;
    }

    /**
     * Indicate that we have a bad value by throwing an exception.
     * This is used in generated methods that return a long.
     * @param errorInfo
     * @param exceptionMessage the exception message
     * @return long NEVER RETURNED.  This method 'returns' an long in order to be usable in
     * long expressions
     */
    static public long badValue_long (ErrorInfo errorInfo, String exceptionMessage) {
        RuntimeException e = new RTValue.InternalException(errorInfo, exceptionMessage);
        if (System.getProperty(LECCMachineConfiguration.DEBUG_INFO_PROP) != null) {
            e.printStackTrace();
        }
        throw e;
    }

    /**
     * Indicate that we have a bad value by throwing an exception.
     * This is used in generated methods that return a Object.
     * @param errorInfo
     * @param exceptionMessage the exception message
     * @return Object NEVER RETURNED.  This method 'returns' an Object in order to be usable in
     * Object expressions
     */
    static public Object badValue_Object (ErrorInfo errorInfo, String exceptionMessage) {
        RuntimeException e = new RTValue.InternalException(errorInfo, exceptionMessage);
        if (System.getProperty(LECCMachineConfiguration.DEBUG_INFO_PROP) != null) {
            e.printStackTrace();
        }
        throw e;
    }

    /**
     * Indicate that we have a bad value by throwing an exception.
     * This is used in generated methods that return a short.
     * @param errorInfo
     * @param exceptionMessage the exception message
     * @return short NEVER RETURNED.  This method 'returns' an short in order to be usable in
     * short expressions
     */
    static public short badValue_short (ErrorInfo errorInfo, String exceptionMessage) {
        RuntimeException e = new RTValue.InternalException(errorInfo, exceptionMessage);
        if (System.getProperty(LECCMachineConfiguration.DEBUG_INFO_PROP) != null) {
            e.printStackTrace();
        }
        throw e;
    }


    /**
     * Indicate that we have a call to the error function.
     * @param errorInfo Description of where the error call was in the source file.  May be null.
     * @param errorMessage the exception message.
     * @return RTValue NEVER RETURNED.  This method 'returns' an RTValue in order to be usable in
     * RTValue expressions
     * @throws CALExecutorException
     */
    static public RTValue errorCall (ErrorInfo errorInfo, String errorMessage) throws CALExecutorException {
        CALExecutorException e = new CALExecutorException.ExternalException.ErrorFunctionException(errorInfo, errorMessage);
        if (System.getProperty(LECCMachineConfiguration.DEBUG_INFO_PROP) != null) {
            e.printStackTrace();
        }
        throw e;
    }

    /**
     * Indicate that we have a call to the error function.
     * This version is called from generated methods that return boolean.
     * @param errorInfo Description of where the error call was in the source file.  May be null.
     * @param errorMessage the exception message.
     * @return boolean NEVER RETURNED.  This method 'returns' an boolean in order to be usable in
     * boolean expressions
     * @throws CALExecutorException
     */
    static public boolean errorCall_boolean  (ErrorInfo errorInfo, String errorMessage) throws CALExecutorException {
        CALExecutorException e = new CALExecutorException.ExternalException.ErrorFunctionException(errorInfo, errorMessage);
        if (System.getProperty(LECCMachineConfiguration.DEBUG_INFO_PROP) != null) {
            e.printStackTrace();
        }
        throw e;
    }

    /**
     * Indicate that we have a call to the error function.
     * This version is called from generated methods that return byte.
     * @param errorInfo Description of where the error call was in the source file.  May be null.
     * @param errorMessage the exception message.
     * @return byte NEVER RETURNED.  This method 'returns' an byte in order to be usable in
     * byte expressions
     * @throws CALExecutorException
     */
    static public byte errorCall_byte (ErrorInfo errorInfo, String errorMessage) throws CALExecutorException {
        CALExecutorException e = new CALExecutorException.ExternalException.ErrorFunctionException(errorInfo, errorMessage);
        if (System.getProperty(LECCMachineConfiguration.DEBUG_INFO_PROP) != null) {
            e.printStackTrace();
        }
        throw e;
    }

    /**
     * Indicate that we have a call to the error function.
     * This version is called from generated methods that return char.
     * @param errorInfo Description of where the error call was in the source file.  May be null.
     * @param errorMessage the exception message.
     * @return char NEVER RETURNED.  This method 'returns' an char in order to be usable in
     * char expressions
     * @throws CALExecutorException
     */
    static public char errorCall_char (ErrorInfo errorInfo, String errorMessage) throws CALExecutorException {
        CALExecutorException e = new CALExecutorException.ExternalException.ErrorFunctionException(errorInfo, errorMessage);
        if (System.getProperty(LECCMachineConfiguration.DEBUG_INFO_PROP) != null) {
            e.printStackTrace();
        }
        throw e;
    }

    /**
     * Indicate that we have a call to the error function.
     * This version is called from generated methods that return double.
     * @param errorInfo Description of where the error call was in the source file.  May be null.
     * @param errorMessage the exception message.
     * @return double NEVER RETURNED.  This method 'returns' an double in order to be usable in
     * double expressions
     * @throws CALExecutorException
     */
    static public double errorCall_double (ErrorInfo errorInfo, String errorMessage) throws CALExecutorException {
        CALExecutorException e = new CALExecutorException.ExternalException.ErrorFunctionException(errorInfo, errorMessage);
        if (System.getProperty(LECCMachineConfiguration.DEBUG_INFO_PROP) != null) {
            e.printStackTrace();
        }
        throw e;
    }

    /**
     * Indicate that we have a call to the error function.
     * This version is called from generated methods that return float.
     * @param errorInfo Description of where the error call was in the source file.  May be null.
     * @param errorMessage the exception message.
     * @return float NEVER RETURNED.  This method 'returns' an float in order to be usable in
     * float expressions
     * @throws CALExecutorException
     */
    static public float errorCall_float  (ErrorInfo errorInfo, String errorMessage) throws CALExecutorException {
        CALExecutorException e = new CALExecutorException.ExternalException.ErrorFunctionException(errorInfo, errorMessage);
        if (System.getProperty(LECCMachineConfiguration.DEBUG_INFO_PROP) != null) {
            e.printStackTrace();
        }
        throw e;
    }

    /**
     * Indicate that we have a call to the error function.
     * This version is called from generated methods that return int.
     * @param errorInfo Description of where the error call was in the source file.  May be null.
     * @param errorMessage the exception message.
     * @return int NEVER RETURNED.  This method 'returns' an int in order to be usable in
     * int expressions
     * @throws CALExecutorException
     */
    static public int errorCall_int  (ErrorInfo errorInfo, String errorMessage) throws CALExecutorException {
        CALExecutorException e = new CALExecutorException.ExternalException.ErrorFunctionException(errorInfo, errorMessage);
        if (System.getProperty(LECCMachineConfiguration.DEBUG_INFO_PROP) != null) {
            e.printStackTrace();
        }
        throw e;
    }

    /**
     * Indicate that we have a call to the error function.
     * This version is called from generated methods that return long.
     * @param errorInfo Description of where the error call was in the source file.  May be null.
     * @param errorMessage the exception message.
     * @return long NEVER RETURNED.  This method 'returns' an long in order to be usable in
     * long expressions
     * @throws CALExecutorException
     */
    static public long errorCall_long (ErrorInfo errorInfo, String errorMessage) throws CALExecutorException {
        CALExecutorException e = new CALExecutorException.ExternalException.ErrorFunctionException(errorInfo, errorMessage);
        if (System.getProperty(LECCMachineConfiguration.DEBUG_INFO_PROP) != null) {
            e.printStackTrace();
        }
        throw e;
    }

    /**
     * Indicate that we have a call to the error function.
     * This version is called from generated methods that return Object.
     * @param errorInfo Description of where the error call was in the source file.  May be null.
     * @param errorMessage the exception message.
     * @return Object NEVER RETURNED.  This method 'returns' an Object in order to be usable in
     * Object expressions
     * @throws CALExecutorException
     */
    static public Object errorCall_Object  (ErrorInfo errorInfo, String errorMessage) throws CALExecutorException {
        CALExecutorException e = new CALExecutorException.ExternalException.ErrorFunctionException(errorInfo, errorMessage);
        if (System.getProperty(LECCMachineConfiguration.DEBUG_INFO_PROP) != null) {
            e.printStackTrace();
        }
        throw e;
    }

    /**
     * Indicate that we have a call to the error function.
     * This version is called from generated methods that return short.
     * @param errorInfo Description of where the error call was in the source file.  May be null.
     * @param errorMessage the exception message.
     * @return short NEVER RETURNED.  This method 'returns' an short in order to be usable in
     * short expressions
     * @throws CALExecutorException
     */
    static public short errorCall_short  (ErrorInfo errorInfo, String errorMessage) throws CALExecutorException {
        CALExecutorException e = new CALExecutorException.ExternalException.ErrorFunctionException(errorInfo, errorMessage);
        if (System.getProperty(LECCMachineConfiguration.DEBUG_INFO_PROP) != null) {
            e.printStackTrace();
        }
        throw e;
    }

    /**
     * Indicate that we have a call to the error function.
     * @param errorMessage the exception message
     * @return RTValue NEVER RETURNED.  This method 'returns' an RTValue in order to be usable in
     * RTValue expressions
     * @throws CALExecutorException
     */
    static public RTValue errorCall (String errorMessage) throws CALExecutorException {
        return errorCall (null, errorMessage);
    }

    ///////////////////////////////////////////////////////////////////////////////////
    // A collection of base version value retrieval methods.  They are all implemented
    // here to avoid doing run-time type checking and casting during the lecc reduction process.

    /**
     * The ordinal value is defined as follows:
     * a) for any data constructor defined in an algebraic data declaration, it is the zero-based ordinal
     *    within the declaration. For example, Prelude.LT = 0, Prelude.Eq = 1, and Prelude.GT = 2.
     * b) for any foreign type with java implementation type int, byte, short, or char, the value is the underlying
     *    value, converted to an int. In particular, this is true of the Prelude.Int, Byte, Short and Char types.
     * c) For the built-in Boolean type: Prelude.False = 0, Prelude.True = 1.
     *
     * For values of other types, such as foreign types other than those described in part b), it throws an exception.
     *
     * @return the ordinal value.
     */
    public int getOrdinalValue() {
        throw new UnsupportedOperationException("Attempt to call getOrdinal() on class: " + getClass().getName());
    }

    /**
     * Return the integer value of this RTValue instance.
     * @return - the integer value of this RTValue
     */
    public int getIntValue () {
        throw new UnsupportedOperationException ("Attempt to call getIntValue() on class: " + getClass().getName());
    }

    /**
     * Retrieve the BigIngegerValue of this RTValue instance
     * @return - the BigInteger value of this RTValue.
     */
    public BigInteger getIntegerValue() {
        throw new UnsupportedOperationException ("Attempt to call getIntegerValue() on class: " + getClass().getName());
    }

    /**
     * Retrieve the boolean of this RTValue instance
     * This is overridden for the True data constructor.  Everything else is considered false.
     * @return - the boolean value of this RTValue.
     */
    public boolean getBooleanValue () {
        throw new UnsupportedOperationException ("Attempt to call getBooleanValue() on class: " + getClass().getName());
    }

    /**
     * Retrieve the byte of this RTValue instance
     * @return - the byte value of this RTValue.
     */
    public byte getByteValue () {
        throw new UnsupportedOperationException ("Attempt to call getByteValue() on class: " + getClass().getName());
    }

    /**
     * Retrieve the char value of this RTValue instance
     * @return - the char value of this RTValue.
     */
    public char getCharValue () {
        throw new UnsupportedOperationException ("Attempt to call getCharValue() on class: " + getClass().getName());
    }

    /**
     * Retrieve the double value of this RTValue instance
     * @return - the double value of this RTValue.
     */
    public double getDoubleValue () {
        throw new UnsupportedOperationException ("Attempt to call getDoubleValue() on class: " + getClass().getName());
    }

    /**
     * Retrieve the float value of this RTValue instance
     * @return - the float value of this RTValue.
     */
    public float getFloatValue () {
        throw new UnsupportedOperationException ("Attempt to call getFloatValue() on class: " + getClass().getName());
    }

    /**
     * Retrieve the long value of this RTValue instance
     * @return - the long value of this RTValue.
     */
    public long getLongValue () {
        throw new UnsupportedOperationException ("Attempt to call getLongValue() on class: " + getClass().getName());
    }

    /**
     * Retrieve the Opaque value of this RTValue instance
     * @return - the Opaque value of this RTValue.
     */
    public Object getOpaqueValue () {
        throw new UnsupportedOperationException ("Attempt to call getOpaqueValue() on class: " + getClass().getName());
    }

    /**
     * Retrieve the short value of this RTValue instance
     * @return - the short value of this RTValue.
     */
    public short getShortValue () {
        throw new UnsupportedOperationException ("Attempt to call getShortValue() on class: " + getClass().getName());
    }

    /**
     * Retrieve the String value of this RTValue instance
     * @return - the String value of this RTValue.
     */
    public String getStringValue () {
        throw new UnsupportedOperationException ("Attempt to call getStringValue() on class: " + getClass().getName());
    }

    ///////////////////////////////////////////////////////////////////////////////////

    /**
     * Implementation of the function.
     * RTSupercombinator subclasses override this to implement their reduction
     * implementation
     * @param rootNode the root node in an application chain.
     * @param ec  the execution context for the current thread.
     * @return RTValue the resulting value
     * @throws CALExecutorException
     */
    protected RTValue f(RTResultFunction rootNode, RTExecutionContext ec) throws CALExecutorException {
        throw new UnsupportedOperationException ("Attempt to call 'f' on: " + getClass().getName());
    }

    /**
     * Implementation of function for one directly passed argument in a strict context.
     * This is used for efficiency when dealing with a fully saturated application
     * RTSupercombinator subclasses override this to implement their reduction
     * implementation.
     * A base implementation is placed in RTValue to avoid run-time type identification
     * and casting when dealing with oversaturated applications.
     * @param arg1 - RTValue. The first argument.
     * @param ec  The execution context for the current thread.
     * @return RTValue the resulting value
     * @throws CALExecutorException
     */
    public RTValue f1S(RTValue arg1, RTExecutionContext ec) throws CALExecutorException {
        throw new UnsupportedOperationException ("Attempt to call 'f1' on: " + getClass().getName());
    }

    /**
     * Implementation of function for two directly passed arguments in a strict context.
     * This is used for efficency when dealing with a fully saturated application
     * RTSupercombinator subclasses override this to implement their reduction
     * implementation
     * A base implementation is placed in RTValue to avoid run-time type identification
     * and casting when dealing with oversaturated applications.
     * @param arg1 - RTValue. The first argument.
     * @param arg2 - RTValue. The second argument.
     * @param ec  The execution context for the current thread.
     * @return RTValue the resulting value
     * @throws CALExecutorException
     */
    public RTValue f2S(RTValue arg1, RTValue arg2, RTExecutionContext ec) throws CALExecutorException {
        throw new UnsupportedOperationException ("Attempt to call 'f2' on: " + getClass().getName());
    }

    /**
     * Implementation of function for three directly passed arguments in a strict context.
     * This is used for efficency when dealing with a fully saturated application
     * RTSupercombinator subclasses override this to implement their reduction
     * implementation
     * @param arg1 - RTValue. The first argument.
     * @param arg2 - RTValue. The second argument.
     * @param arg3 - RTValue. The third argument.
     * @param ec  The execution context for the current thread.
     * @return RTValue the resulting value
     * @throws CALExecutorException
     */
    public RTValue f3S(RTValue arg1, RTValue arg2, RTValue arg3, RTExecutionContext ec) throws CALExecutorException {
        throw new UnsupportedOperationException ("Attempt to call 'f3' on: " + getClass().getName());
    }

    /**
     * Implementation of function for four directly passed arguments in a strict context.
     * This is used for efficency when dealing with a fully saturated application
     * RTSupercombinator subclasses override this to implement their reduction
     * implementation
     * @param arg1 - RTValue. The first argument.
     * @param arg2 - RTValue. The second argument.
     * @param arg3 - RTValue. The third argument.
     * @param arg4 - RTValue. The fourth argument.
     * @param ec  The execution context for the current thread.
     * @return RTValue the resulting value
     * @throws CALExecutorException
     */
    public RTValue f4S(RTValue arg1, RTValue arg2, RTValue arg3, RTValue arg4, RTExecutionContext ec) throws CALExecutorException {
        throw new UnsupportedOperationException ("Attempt to call 'f4' on: " + getClass().getName());
    }

    /**
     * Implementation of function for five directly passed arguments in a strict context.
     * This is used for efficiency when dealing with a fully saturated application
     * RTSupercombinator subclasses override this to implement their reduction
     * implementation
     * @param arg1 - RTValue. The first argument.
     * @param arg2 - RTValue. The second argument.
     * @param arg3 - RTValue. The third argument.
     * @param arg4 - RTValue. The fourth argument.
     * @param arg5 - RTValue. The fifth argument.
     * @param ec  The execution context for the current thread.
     * @return RTValue the resulting value
     * @throws CALExecutorException
     */
    public RTValue f5S(RTValue arg1, RTValue arg2, RTValue arg3, RTValue arg4, RTValue arg5, RTExecutionContext ec) throws CALExecutorException {
        throw new UnsupportedOperationException ("Attempt to call 'f5' on: " + getClass().getName());
    }

    /**
     * Implementation of function for six directly passed arguments in a strict context.
     * This is used for efficiency when dealing with a fully saturated application
     * RTSupercombinator subclasses override this to implement their reduction
     * implementation
     * @param arg1 - RTValue. The first argument.
     * @param arg2 - RTValue. The second argument.
     * @param arg3 - RTValue. The third argument.
     * @param arg4 - RTValue. The fourth argument.
     * @param arg5 - RTValue. The fifth argument.
     * @param arg6 - RTValue. The sixth argument.
     * @param ec  The execution context for the current thread.
     * @return RTValue the resulting value
     * @throws CALExecutorException
     */
    public RTValue f6S(RTValue arg1, RTValue arg2, RTValue arg3, RTValue arg4, RTValue arg5, RTValue arg6, RTExecutionContext ec) throws CALExecutorException {
        throw new UnsupportedOperationException ("Attempt to call 'f6' on: " + getClass().getName());
    }

    /**
     * Implementation of function for seven directly passed arguments in a strict context.
     * This is used for efficiency when dealing with a fully saturated application
     * RTSupercombinator subclasses override this to implement their reduction
     * implementation
     * @param arg1 - RTValue. The first argument.
     * @param arg2 - RTValue. The second argument.
     * @param arg3 - RTValue. The third argument.
     * @param arg4 - RTValue. The fourth argument.
     * @param arg5 - RTValue. The fifth argument.
     * @param arg6 - RTValue. The sixth argument.
     * @param arg7 - RTValue. The seventh argument.
     * @param ec  The execution context for the current thread.
     * @return RTValue the resulting value
     * @throws CALExecutorException
     */
    public RTValue f7S(RTValue arg1, RTValue arg2, RTValue arg3, RTValue arg4, RTValue arg5, RTValue arg6, RTValue arg7, RTExecutionContext ec) throws CALExecutorException {
        throw new UnsupportedOperationException ("Attempt to call 'f7' on: " + getClass().getName());
    }

    /**
     * Implementation of function for eight directly passed arguments in a strict context.
     * This is used for efficiency when dealing with a fully saturated application
     * RTSupercombinator subclasses override this to implement their reduction
     * implementation
     * @param arg1 - RTValue. The first argument.
     * @param arg2 - RTValue. The second argument.
     * @param arg3 - RTValue. The third argument.
     * @param arg4 - RTValue. The fourth argument.
     * @param arg5 - RTValue. The fifth argument.
     * @param arg6 - RTValue. The sixth argument.
     * @param arg7 - RTValue. The seventh argument.
     * @param arg8 - RTValue. The eighth argument.
     * @param ec  The execution context for the current thread.
     * @return RTValue the resulting value
     * @throws CALExecutorException
     */
    public RTValue f8S(RTValue arg1, RTValue arg2, RTValue arg3, RTValue arg4, RTValue arg5, RTValue arg6, RTValue arg7, RTValue arg8, RTExecutionContext ec) throws CALExecutorException {
        throw new UnsupportedOperationException ("Attempt to call 'f8' on: " + getClass().getName());
    }

    /**
     * Implementation of function for nine directly passed arguments in a strict context.
     * This is used for efficiency when dealing with a fully saturated application
     * RTSupercombinator subclasses override this to implement their reduction
     * implementation
     * @param arg1 - RTValue. The first argument.
     * @param arg2 - RTValue. The second argument.
     * @param arg3 - RTValue. The third argument.
     * @param arg4 - RTValue. The fourth argument.
     * @param arg5 - RTValue. The fifth argument.
     * @param arg6 - RTValue. The sixth argument.
     * @param arg7 - RTValue. The seventh argument.
     * @param arg8 - RTValue. The eighth argument.
     * @param arg9
     * @param ec  The execution context for the current thread.
     * @return RTValue the resulting value
     * @throws CALExecutorException
     */
    public RTValue f9S(RTValue arg1,
                          RTValue arg2,
                          RTValue arg3,
                          RTValue arg4,
                          RTValue arg5,
                          RTValue arg6,
                          RTValue arg7,
                          RTValue arg8,
                          RTValue arg9,
                          RTExecutionContext ec) throws CALExecutorException {
        throw new UnsupportedOperationException ("Attempt to call 'f9' on: " + getClass().getName());
    }

    /**
     * Implementation of function for ten directly passed arguments in a strict context.
     * This is used for efficiency when dealing with a fully saturated application
     * RTSupercombinator subclasses override this to implement their reduction
     * implementation
     * @param arg1 - RTValue. The first argument.
     * @param arg2 - RTValue. The second argument.
     * @param arg3 - RTValue. The third argument.
     * @param arg4 - RTValue. The fourth argument.
     * @param arg5 - RTValue. The fifth argument.
     * @param arg6 - RTValue. The sixth argument.
     * @param arg7 - RTValue. The seventh argument.
     * @param arg8 - RTValue. The eighth argument.
     * @param arg9
     * @param arg10
     * @param ec  The execution context for the current thread.
     * @return RTValue the resulting value
     * @throws CALExecutorException
     */
    public RTValue f10S(RTValue arg1,
                          RTValue arg2,
                          RTValue arg3,
                          RTValue arg4,
                          RTValue arg5,
                          RTValue arg6,
                          RTValue arg7,
                          RTValue arg8,
                          RTValue arg9,
                          RTValue arg10,
                          RTExecutionContext ec) throws CALExecutorException {
        throw new UnsupportedOperationException ("Attempt to call 'f10' on: " + getClass().getName());
    }
    /**
     * Implementation of function for eleven directly passed arguments in a strict context.
     * This is used for efficiency when dealing with a fully saturated application
     * RTSupercombinator subclasses override this to implement their reduction
     * implementation
     * @param arg1 - RTValue. The first argument.
     * @param arg2 - RTValue. The second argument.
     * @param arg3 - RTValue. The third argument.
     * @param arg4 - RTValue. The fourth argument.
     * @param arg5 - RTValue. The fifth argument.
     * @param arg6 - RTValue. The sixth argument.
     * @param arg7 - RTValue. The seventh argument.
     * @param arg8 - RTValue. The eighth argument.
     * @param arg9
     * @param arg10
     * @param arg11
     * @param ec  The execution context for the current thread.
     * @return RTValue the resulting value
     * @throws CALExecutorException
     */
    public RTValue f11S(RTValue arg1,
                          RTValue arg2,
                          RTValue arg3,
                          RTValue arg4,
                          RTValue arg5,
                          RTValue arg6,
                          RTValue arg7,
                          RTValue arg8,
                          RTValue arg9,
                          RTValue arg10,
                          RTValue arg11,
                          RTExecutionContext ec) throws CALExecutorException {
        throw new UnsupportedOperationException ("Attempt to call 'f11' on: " + getClass().getName());
    }

    /**
     * Implementation of function for twelve directly passed arguments in a strict context.
     * This is used for efficiency when dealing with a fully saturated application
     * RTSupercombinator subclasses override this to implement their reduction
     * implementation
     * @param arg1 - RTValue. The first argument.
     * @param arg2 - RTValue. The second argument.
     * @param arg3 - RTValue. The third argument.
     * @param arg4 - RTValue. The fourth argument.
     * @param arg5 - RTValue. The fifth argument.
     * @param arg6 - RTValue. The sixth argument.
     * @param arg7 - RTValue. The seventh argument.
     * @param arg8 - RTValue. The eighth argument.
     * @param arg9
     * @param arg10
     * @param arg11
     * @param arg12
     * @param ec  The execution context for the current thread.
     * @return RTValue the resulting value
     * @throws CALExecutorException
     */
    public RTValue f12S(RTValue arg1,
                          RTValue arg2,
                          RTValue arg3,
                          RTValue arg4,
                          RTValue arg5,
                          RTValue arg6,
                          RTValue arg7,
                          RTValue arg8,
                          RTValue arg9,
                          RTValue arg10,
                          RTValue arg11,
                          RTValue arg12,
                          RTExecutionContext ec) throws CALExecutorException {
        throw new UnsupportedOperationException ("Attempt to call 'f12' on: " + getClass().getName());
    }

    /**
     * Implementation of function for thirteen directly passed arguments in a strict context.
     * This is used for efficiency when dealing with a fully saturated application
     * RTSupercombinator subclasses override this to implement their reduction
     * implementation
     * @param arg1 - RTValue. The first argument.
     * @param arg2 - RTValue. The second argument.
     * @param arg3 - RTValue. The third argument.
     * @param arg4 - RTValue. The fourth argument.
     * @param arg5 - RTValue. The fifth argument.
     * @param arg6 - RTValue. The sixth argument.
     * @param arg7 - RTValue. The seventh argument.
     * @param arg8 - RTValue. The eighth argument.
     * @param arg9
     * @param arg10
     * @param arg11
     * @param arg12
     * @param arg13
     * @param ec  The execution context for the current thread.
     * @return RTValue the resulting value
     * @throws CALExecutorException
     */
    public RTValue f13S(RTValue arg1,
                          RTValue arg2,
                          RTValue arg3,
                          RTValue arg4,
                          RTValue arg5,
                          RTValue arg6,
                          RTValue arg7,
                          RTValue arg8,
                          RTValue arg9,
                          RTValue arg10,
                          RTValue arg11,
                          RTValue arg12,
                          RTValue arg13,
                          RTExecutionContext ec) throws CALExecutorException {
        throw new UnsupportedOperationException ("Attempt to call 'f13' on: " + getClass().getName());
    }

    /**
     * Implementation of function for fourteen directly passed arguments in a strict context.
     * This is used for efficiency when dealing with a fully saturated application
     * RTSupercombinator subclasses override this to implement their reduction
     * implementation
     * @param arg1 - RTValue. The first argument.
     * @param arg2 - RTValue. The second argument.
     * @param arg3 - RTValue. The third argument.
     * @param arg4 - RTValue. The fourth argument.
     * @param arg5 - RTValue. The fifth argument.
     * @param arg6 - RTValue. The sixth argument.
     * @param arg7 - RTValue. The seventh argument.
     * @param arg8 - RTValue. The eighth argument.
     * @param arg9
     * @param arg10
     * @param arg11
     * @param arg12
     * @param arg13
     * @param arg14
     * @param ec  The execution context for the current thread.
     * @return RTValue the resulting value
     * @throws CALExecutorException
     */
    public RTValue f14S(RTValue arg1,
                          RTValue arg2,
                          RTValue arg3,
                          RTValue arg4,
                          RTValue arg5,
                          RTValue arg6,
                          RTValue arg7,
                          RTValue arg8,
                          RTValue arg9,
                          RTValue arg10,
                          RTValue arg11,
                          RTValue arg12,
                          RTValue arg13,
                          RTValue arg14,
                          RTExecutionContext ec) throws CALExecutorException {
        throw new UnsupportedOperationException ("Attempt to call 'f14' on: " + getClass().getName());
    }

    /**
     * Implementation of function for fifteen directly passed arguments in a strict context.
     * This is used for efficiency when dealing with a fully saturated application
     * RTSupercombinator subclasses override this to implement their reduction
     * implementation
     * @param arg1 - RTValue. The first argument.
     * @param arg2 - RTValue. The second argument.
     * @param arg3 - RTValue. The third argument.
     * @param arg4 - RTValue. The fourth argument.
     * @param arg5 - RTValue. The fifth argument.
     * @param arg6 - RTValue. The sixth argument.
     * @param arg7 - RTValue. The seventh argument.
     * @param arg8 - RTValue. The eighth argument.
     * @param arg9
     * @param arg10
     * @param arg11
     * @param arg12
     * @param arg13
     * @param arg14
     * @param arg15
     * @param ec  The execution context for the current thread.
     * @return RTValue the resulting value
     * @throws CALExecutorException
     */
    public RTValue f15S(RTValue arg1,
                          RTValue arg2,
                          RTValue arg3,
                          RTValue arg4,
                          RTValue arg5,
                          RTValue arg6,
                          RTValue arg7,
                          RTValue arg8,
                          RTValue arg9,
                          RTValue arg10,
                          RTValue arg11,
                          RTValue arg12,
                          RTValue arg13,
                          RTValue arg14,
                          RTValue arg15,
                          RTExecutionContext ec) throws CALExecutorException {
        throw new UnsupportedOperationException ("Attempt to call 'f15' on: " + getClass().getName());
    }

    /**
     * Implementation of function for one directly passed argument in a lazy context.
     * In the case of a non-supercombinator node (such as this) all we can do is
     * apply the arguments to this (i.e. build an application graph) and return.
     * Supercombinator classes can override this method to implement the function logic.
     * @param arg1 - RTValue. The first argument.
     * @param ec  The execution context for the current thread.
     * @return RTValue the resulting value
     * @throws CALExecutorException
     */
    public RTValue f1L(RTValue arg1, RTExecutionContext ec) throws CALExecutorException{
        return new RTApplication(this,arg1);
    }

    /**
     * Implementation of function for two directly passed arguments in a lazy context.
     * In the case of a non-supercombinator node (such as this) all we can do is
     * apply the arguments to this (i.e. build an application graph) and return.
     * Supercombinator classes can override this method to implement the function logic.
     * @param arg1 - RTValue. The first argument.
     * @param arg2 - RTValue. The second argument.
     * @param ec  The execution context for the current thread.
     * @return RTValue the resulting value
     * @throws CALExecutorException
     */
    public RTValue f2L(RTValue arg1, RTValue arg2, RTExecutionContext ec) throws CALExecutorException{
        return new RTApplication (new RTApplication(this, arg1), arg2);
    }

    /**
     * Implementation of function for three directly passed arguments in a lazy context.
     * In the case of a non-supercombinator node (such as this) all we can do is
     * apply the arguments to this (i.e. build an application graph) and return.
     * Supercombinator classes can override this method to implement the function logic.
     * @param arg1 - RTValue. The first argument.
     * @param arg2 - RTValue. The second argument.
     * @param arg3 - RTValue. The third argument.
     * @param ec  The execution context for the current thread.
     * @return RTValue the resulting value
     * @throws CALExecutorException
     */
    public RTValue f3L(RTValue arg1, RTValue arg2, RTValue arg3, RTExecutionContext ec) throws CALExecutorException{
        return new RTApplication (new RTApplication (new RTApplication(this, arg1), arg2), arg3);
    }

    /**
     * Implementation of function for four directly passed arguments in a lazy context.
     * In the case of a non-supercombinator node (such as this) all we can do is
     * apply the arguments to this (i.e. build an application graph) and return.
     * Supercombinator classes can override this method to implement the function logic.
     * @param arg1 - RTValue. The first argument.
     * @param arg2 - RTValue. The second argument.
     * @param arg3 - RTValue. The third argument.
     * @param arg4 - RTValue. The fourth argument.
     * @param ec  The execution context for the current thread.
     * @return RTValue the resulting value
     * @throws CALExecutorException
     */
    public RTValue f4L(RTValue arg1, RTValue arg2, RTValue arg3, RTValue arg4, RTExecutionContext ec) throws CALExecutorException{
        return new RTApplication(new RTApplication (new RTApplication (new RTApplication(this, arg1), arg2), arg3), arg4);
    }

    /**
     * Implementation of function for five directly passed arguments in a lazy context.
     * In the case of a non-supercombinator node (such as this) all we can do is
     * apply the arguments to this (i.e. build an application graph) and return.
     * Supercombinator classes can override this method to implement the function logic.
     * @param arg1 - RTValue. The first argument.
     * @param arg2 - RTValue. The second argument.
     * @param arg3 - RTValue. The third argument.
     * @param arg4 - RTValue. The fourth argument.
     * @param arg5 - RTValue. The fifth argument.
     * @param ec  The execution context for the current thread.
     * @return RTValue the resulting value
     * @throws CALExecutorException
     */
    public RTValue f5L(RTValue arg1, RTValue arg2, RTValue arg3, RTValue arg4, RTValue arg5, RTExecutionContext ec) throws CALExecutorException {
        return new RTApplication (new RTApplication(new RTApplication (new RTApplication (new RTApplication(this, arg1), arg2), arg3), arg4), arg5);
    }

    /**
     * Implementation of function for six directly passed arguments in a lazy context.
     * In the case of a non-supercombinator node (such as this) all we can do is
     * apply the arguments to this (i.e. build an application graph) and return.
     * Supercombinator classes can override this method to implement the function logic.
     * @param arg1 - RTValue. The first argument.
     * @param arg2 - RTValue. The second argument.
     * @param arg3 - RTValue. The third argument.
     * @param arg4 - RTValue. The fourth argument.
     * @param arg5 - RTValue. The fifth argument.
     * @param arg6 - RTValue. The sixth argument.
     * @param ec  The execution context for the current thread.
     * @return RTValue the resulting value
     * @throws CALExecutorException
     */
    public RTValue f6L(RTValue arg1, RTValue arg2, RTValue arg3, RTValue arg4, RTValue arg5, RTValue arg6, RTExecutionContext ec) throws CALExecutorException {
        return new RTApplication (new RTApplication (new RTApplication(new RTApplication (new RTApplication (new RTApplication(this, arg1), arg2), arg3), arg4), arg5), arg6);
    }

    /**
     * Implementation of function for seven directly passed arguments in a strict context.
     * In the case of a non-supercombinator node (such as this) all we can do is
     * apply the arguments to this (i.e. build an application graph) and return.
     * Supercombinator classes can override this method to implement the function logic.
     * @param arg1 - RTValue. The first argument.
     * @param arg2 - RTValue. The second argument.
     * @param arg3 - RTValue. The third argument.
     * @param arg4 - RTValue. The fourth argument.
     * @param arg5 - RTValue. The fifth argument.
     * @param arg6 - RTValue. The sixth argument.
     * @param arg7 - RTValue. The seventh argument.
     * @param ec  The execution context for the current thread.
     * @return RTValue the resulting value
     * @throws CALExecutorException
     */
    public RTValue f7L(RTValue arg1, RTValue arg2, RTValue arg3, RTValue arg4, RTValue arg5, RTValue arg6, RTValue arg7, RTExecutionContext ec) throws CALExecutorException {
        return new RTApplication (new RTApplication (new RTApplication (new RTApplication(new RTApplication (new RTApplication (new RTApplication(this, arg1), arg2), arg3), arg4), arg5), arg6), arg7);
    }

    /**
     * Implementation of function for eight directly passed arguments in a lazy context.
     * In the case of a non-supercombinator node (such as this) all we can do is
     * apply the arguments to this (i.e. build an application graph) and return.
     * Supercombinator classes can override this method to implement the function logic.
     * @param arg1 - RTValue. The first argument.
     * @param arg2 - RTValue. The second argument.
     * @param arg3 - RTValue. The third argument.
     * @param arg4 - RTValue. The fourth argument.
     * @param arg5 - RTValue. The fifth argument.
     * @param arg6 - RTValue. The sixth argument.
     * @param arg7 - RTValue. The seventh argument.
     * @param arg8 - RTValue. The eighth argument.
     * @param ec  The execution context for the current thread.
     * @return RTValue the resulting value
     * @throws CALExecutorException
     */
    public RTValue f8L(RTValue arg1, RTValue arg2, RTValue arg3, RTValue arg4, RTValue arg5, RTValue arg6, RTValue arg7, RTValue arg8, RTExecutionContext ec) throws CALExecutorException {
        return new RTApplication(new RTApplication (new RTApplication (new RTApplication (new RTApplication(new RTApplication (new RTApplication (new RTApplication(this, arg1), arg2), arg3), arg4), arg5), arg6), arg7), arg8);
    }

    /**
     * Implementation of function for nine directly passed arguments in a lazy context.
     * Since this is a fully saturated application all we can do is build an application
     * graph and return.
     * Supercombinator classes can override this method to implement the function logic.
     * @param arg1 - RTValue. The first argument.
     * @param arg2 - RTValue. The second argument.
     * @param arg3 - RTValue. The third argument.
     * @param arg4 - RTValue. The fourth argument.
     * @param arg5 - RTValue. The fifth argument.
     * @param arg6 - RTValue. The sixth argument.
     * @param arg7 - RTValue. The seventh argument.
     * @param arg8 - RTValue. The eighth argument.
     * @param arg9
     * @param ec  The execution context for the current thread.
     * @return RTValue the resulting value
     * @throws CALExecutorException
     */
    public RTValue f9L(RTValue arg1,
                          RTValue arg2,
                          RTValue arg3,
                          RTValue arg4,
                          RTValue arg5,
                          RTValue arg6,
                          RTValue arg7,
                          RTValue arg8,
                          RTValue arg9,
                          RTExecutionContext ec) throws CALExecutorException {
        return new RTApplication (new RTApplication(new RTApplication (new RTApplication (new RTApplication (new RTApplication(new RTApplication (new RTApplication (new RTApplication(this, arg1), arg2), arg3), arg4), arg5), arg6), arg7), arg8), arg9);
    }

    /**
     * Implementation of function for ten directly passed arguments in a lazy context.
     * Since this is a fully saturated application all we can do is build an application
     * graph and return.
     * Supercombinator classes can override this method to implement the function logic.
     * @param arg1 - RTValue. The first argument.
     * @param arg2 - RTValue. The second argument.
     * @param arg3 - RTValue. The third argument.
     * @param arg4 - RTValue. The fourth argument.
     * @param arg5 - RTValue. The fifth argument.
     * @param arg6 - RTValue. The sixth argument.
     * @param arg7 - RTValue. The seventh argument.
     * @param arg8 - RTValue. The eighth argument.
     * @param arg9
     * @param arg10
     * @param ec  The execution context for the current thread.
     * @return RTValue the resulting value
     * @throws CALExecutorException
     */
    public RTValue f10L(RTValue arg1,
                          RTValue arg2,
                          RTValue arg3,
                          RTValue arg4,
                          RTValue arg5,
                          RTValue arg6,
                          RTValue arg7,
                          RTValue arg8,
                          RTValue arg9,
                          RTValue arg10,
                          RTExecutionContext ec) throws CALExecutorException {
        return new RTApplication (new RTApplication (new RTApplication(new RTApplication (new RTApplication (new RTApplication (new RTApplication(new RTApplication (new RTApplication (new RTApplication(this, arg1), arg2), arg3), arg4), arg5), arg6), arg7), arg8), arg9), arg10);
    }

    /**
     * Implementation of function for eleven directly passed arguments in a lazy context.
     * Since this is a fully saturated application all we can do is build an application
     * graph and return.
     * Supercombinator classes can override this method to implement the function logic.
     * @param arg1 - RTValue. The first argument.
     * @param arg2 - RTValue. The second argument.
     * @param arg3 - RTValue. The third argument.
     * @param arg4 - RTValue. The fourth argument.
     * @param arg5 - RTValue. The fifth argument.
     * @param arg6 - RTValue. The sixth argument.
     * @param arg7 - RTValue. The seventh argument.
     * @param arg8 - RTValue. The eighth argument.
     * @param arg9
     * @param arg10
     * @param arg11
     * @param ec  The execution context for the current thread.
     * @return RTValue the resulting value
     * @throws CALExecutorException
     */
    public RTValue f11L(RTValue arg1,
                          RTValue arg2,
                          RTValue arg3,
                          RTValue arg4,
                          RTValue arg5,
                          RTValue arg6,
                          RTValue arg7,
                          RTValue arg8,
                          RTValue arg9,
                          RTValue arg10,
                          RTValue arg11,
                          RTExecutionContext ec) throws CALExecutorException {
        return new RTApplication(new RTApplication (new RTApplication (new RTApplication(new RTApplication (new RTApplication (new RTApplication (new RTApplication(new RTApplication (new RTApplication (new RTApplication(this, arg1), arg2), arg3), arg4), arg5), arg6), arg7), arg8), arg9), arg10), arg11);
    }


    /**
     * Implementation of function for twelve directly passed arguments in a strict context.
     * Since this is a fully saturated application all we can do is build an application
     * graph and return.
     * Supercombinator classes can override this method to implement the function logic.
     * @param arg1 - RTValue. The first argument.
     * @param arg2 - RTValue. The second argument.
     * @param arg3 - RTValue. The third argument.
     * @param arg4 - RTValue. The fourth argument.
     * @param arg5 - RTValue. The fifth argument.
     * @param arg6 - RTValue. The sixth argument.
     * @param arg7 - RTValue. The seventh argument.
     * @param arg8 - RTValue. The eighth argument.
     * @param arg9
     * @param arg10
     * @param arg11
     * @param arg12
     * @param ec  The execution context for the current thread.
     * @return RTValue the resulting value
     * @throws CALExecutorException
     */
    public RTValue f12L(RTValue arg1,
                          RTValue arg2,
                          RTValue arg3,
                          RTValue arg4,
                          RTValue arg5,
                          RTValue arg6,
                          RTValue arg7,
                          RTValue arg8,
                          RTValue arg9,
                          RTValue arg10,
                          RTValue arg11,
                          RTValue arg12,
                          RTExecutionContext ec) throws CALExecutorException {
        return new RTApplication (new RTApplication(new RTApplication (new RTApplication (new RTApplication(new RTApplication (new RTApplication (new RTApplication (new RTApplication(new RTApplication (new RTApplication (new RTApplication(this, arg1), arg2), arg3), arg4), arg5), arg6), arg7), arg8), arg9), arg10), arg11), arg12);
    }

    /**
     * Implementation of function for thirteen directly passed arguments in a lazy context.
     * Since this is a fully saturated application all we can do is build an application
     * graph and return.
     * Supercombinator classes can override this method to implement the function logic.
     * @param arg1 - RTValue. The first argument.
     * @param arg2 - RTValue. The second argument.
     * @param arg3 - RTValue. The third argument.
     * @param arg4 - RTValue. The fourth argument.
     * @param arg5 - RTValue. The fifth argument.
     * @param arg6 - RTValue. The sixth argument.
     * @param arg7 - RTValue. The seventh argument.
     * @param arg8 - RTValue. The eighth argument.
     * @param arg9
     * @param arg10
     * @param arg11
     * @param arg12
     * @param arg13
     * @param ec  The execution context for the current thread.
     * @return RTValue the resulting value
     * @throws CALExecutorException
     */
    public RTValue f13L(RTValue arg1,
                          RTValue arg2,
                          RTValue arg3,
                          RTValue arg4,
                          RTValue arg5,
                          RTValue arg6,
                          RTValue arg7,
                          RTValue arg8,
                          RTValue arg9,
                          RTValue arg10,
                          RTValue arg11,
                          RTValue arg12,
                          RTValue arg13,
                          RTExecutionContext ec) throws CALExecutorException {
        return new RTApplication(new RTApplication (new RTApplication(new RTApplication (new RTApplication (new RTApplication(new RTApplication (new RTApplication (new RTApplication (new RTApplication(new RTApplication (new RTApplication (new RTApplication(this, arg1), arg2), arg3), arg4), arg5), arg6), arg7), arg8), arg9), arg10), arg11), arg12), arg13);
    }

    /**
     * Implementation of function for fourteen directly passed arguments in a strict context.
     * Since this is a fully saturated application all we can do is build an application
     * graph and return.
     * Supercombinator classes can override this method to implement the function logic.
     * @param arg1 - RTValue. The first argument.
     * @param arg2 - RTValue. The second argument.
     * @param arg3 - RTValue. The third argument.
     * @param arg4 - RTValue. The fourth argument.
     * @param arg5 - RTValue. The fifth argument.
     * @param arg6 - RTValue. The sixth argument.
     * @param arg7 - RTValue. The seventh argument.
     * @param arg8 - RTValue. The eighth argument.
     * @param arg9
     * @param arg10
     * @param arg11
     * @param arg12
     * @param arg13
     * @param arg14
     * @param ec  The execution context for the current thread.
     * @return RTValue the resulting value
     * @throws CALExecutorException
     */
    public RTValue f14L(RTValue arg1,
                          RTValue arg2,
                          RTValue arg3,
                          RTValue arg4,
                          RTValue arg5,
                          RTValue arg6,
                          RTValue arg7,
                          RTValue arg8,
                          RTValue arg9,
                          RTValue arg10,
                          RTValue arg11,
                          RTValue arg12,
                          RTValue arg13,
                          RTValue arg14,
                          RTExecutionContext ec) throws CALExecutorException {
        return new RTApplication(new RTApplication(new RTApplication (new RTApplication(new RTApplication (new RTApplication (new RTApplication(new RTApplication (new RTApplication (new RTApplication (new RTApplication(new RTApplication (new RTApplication (new RTApplication(this, arg1), arg2), arg3), arg4), arg5), arg6), arg7), arg8), arg9), arg10), arg11), arg12), arg13), arg14);
    }

    /**
     * Implementation of function for fifteen directly passed arguments in a lazy context.
     * Since this is a fully saturated application all we can do is build an application
     * graph and return.
     * Supercombinator classes can override this method to implement the function logic.
     * @param arg1 - RTValue. The first argument.
     * @param arg2 - RTValue. The second argument.
     * @param arg3 - RTValue. The third argument.
     * @param arg4 - RTValue. The fourth argument.
     * @param arg5 - RTValue. The fifth argument.
     * @param arg6 - RTValue. The sixth argument.
     * @param arg7 - RTValue. The seventh argument.
     * @param arg8 - RTValue. The eighth argument.
     * @param arg9
     * @param arg10
     * @param arg11
     * @param arg12
     * @param arg13
     * @param arg14
     * @param arg15
     * @param ec  The execution context for the current thread.
     * @return RTValue the resulting value
     * @throws CALExecutorException
     */
    public RTValue f15L(RTValue arg1,
                          RTValue arg2,
                          RTValue arg3,
                          RTValue arg4,
                          RTValue arg5,
                          RTValue arg6,
                          RTValue arg7,
                          RTValue arg8,
                          RTValue arg9,
                          RTValue arg10,
                          RTValue arg11,
                          RTValue arg12,
                          RTValue arg13,
                          RTValue arg14,
                          RTValue arg15,
                          RTExecutionContext ec) throws CALExecutorException {
        return new RTApplication (new RTApplication(new RTApplication(new RTApplication (new RTApplication(new RTApplication (new RTApplication (new RTApplication(new RTApplication (new RTApplication (new RTApplication (new RTApplication(new RTApplication (new RTApplication (new RTApplication(this, arg1), arg2), arg3), arg4), arg5), arg6), arg7), arg8), arg9), arg10), arg11), arg12), arg13), arg14), arg15);
    }



    /**
     * A utility function used by the generated runtime classes to build an
     * error message when a foreign function throws an exception.
     * @param thrown
     * @param callingClassName - the name of the generated class where the exception was caught
     * @param callingModuleName - the module name of the function where the exception was caught
     * @param callingFunctionName - the name of the function where the module was caught.
     * @return String
     */
    protected static String generateForeignFunctionErrorMessage (
            Throwable thrown,
            String callingClassName,
            String callingModuleName,
            String callingFunctionName) {

        // When an exception is thrown from a foreign function
        //(i.e. an external java function) called from CAL we catch the
        // exception and wrap it in an RTForeignFunctionException.
        // We try to generate a message for the RTForeignFunctionException that
        // will give the CAL user useful information about where the foreign
        // exception occurred.  So we try to indicate where in the CAL code the
        // call to the foreign function occurred, as well as where the foreign
        // exception was actually thrown.

        // Get the stack trace from the thrown exception.
        // We want to determine at least two things: 1) which foreign
        // function threw the exception, 2) which CAL function called
        // the foreign function.
        // We know the class name of the generated class which called the foreign
        // function (i.e. the callingClassName argument).  We look in the stack trace
        // until we find the element for this class.  At this point we know the
        // previous item in the stack trace is for the foreign function that was called.
        // This won't necessarily be the top of the stack trace since the actual
        // thrown exception may have originated in the body of the foreign function.

        String message = "Foreign Function error: ";

        StackTraceElement[] stack = thrown.getStackTrace();
        int calledFromIndex = 0;
        for (int i = 0; i < stack.length-1; i++) {
            calledFromIndex = i+1;
            StackTraceElement next = stack[calledFromIndex];
            String className = next.getClassName();
            if (className.equals(callingClassName)) {
                // We know that the next frame (i+1) is the frame for the
                // CAL class that called the foreign function.  This means
                // that the current frame (i) is the frame for the called
                // foreign function.
                String foreignFunctionClassName = stack[i].getClassName();
                int p1 = foreignFunctionClassName.lastIndexOf(".") + 1;
                foreignFunctionClassName = foreignFunctionClassName.substring(p1);
                String foreignFunctionMethodName = stack[i].getMethodName();
                message = "The exception " + thrown.getClass().getName() + " occurred while calling " + foreignFunctionClassName + "." + foreignFunctionMethodName + "() from " + callingModuleName + "." + callingFunctionName + ".";
                break;
            }
        }

        return message;
    }

    /**
     * Exception to use if there is a bug in CAL's Java implementation.
     * This should never be thrown as a result of run-time errors
     * generated by the user.
     *
     * @author Bo Ilic
     */
    static public final class InternalException extends RuntimeException {

        private static final long serialVersionUID = 3338625933405407017L;

        /** <code>errorInfo</code> may be null. */
        private final transient ErrorInfo errorInfo;

        /**
         * Constructor for Exception.
         * @param errorInfo source positional information of where the intereral error may have occurred.
         * @param message
         */
        InternalException(ErrorInfo errorInfo, String message) {
            super(message, (Throwable)null);
            this.errorInfo = errorInfo;
        }

        /**
         * @return The error info object. May be null.
         */
        public ErrorInfo getErrorInfo(){
            return errorInfo;
        }
    }

    // These methods are used in the Java source generation to change
    // a Java expression that is not a valid statement to a valid statement.
    // For example: '1 + 2;' is not a valid Java statement, but 'doNothing(1+2);' is.
    // This is primarily used when dealing with a top-level application of Prelude.seq
    // and generating a separate Java statement for each of its arguments.
    public static void doNothing (Object o) {/* This space intentionally left empty.*/}
    public static void doNothing (boolean o) {/* This space intentionally left empty.*/}
    public static void doNothing (byte o) {/* This space intentionally left empty.*/}
    public static void doNothing (char o) {/* This space intentionally left empty.*/}
    public static void doNothing (double o) {/* This space intentionally left empty.*/}
    public static void doNothing (float o) {/* This space intentionally left empty.*/}
    public static void doNothing (int o) {/* This space intentionally left empty.*/}
    public static void doNothing (long o) {/* This space intentionally left empty.*/}
    public static void doNothing (short o) {/* This space intentionally left empty.*/}

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract int debug_getNChildren();
    /**
     * {@inheritDoc}
     */
    @Override
    public abstract CalValue debug_getChild(int childN);
    /**
     * {@inheritDoc}
     */
    @Override
    public abstract String debug_getNodeStartText();
    /**
     * {@inheritDoc}
     */
    @Override
    public abstract String debug_getNodeEndText();
    /**
     * {@inheritDoc}
     */
    @Override
    public abstract String debug_getChildPrefixText(int childN);
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean debug_isIndirectionNode() {
        //must be overridden in RTResultFunction, but in general nodes are not indirection nodes.
        return false;
    }

    /**
     * Function used for generalized traversal to the
     * left hand side of a program graph.
     * This function will return the graph node immediately to the left
     * or null if there is none.
     * @return the next graph node to the left, null if there is nothing to the left.
     */
    RTValue lhs () {
        return null;
    }

    /**
     * Function to provide the message delivered when constructor argument
     * assertions fail in a generated derived class.  We use this function,
     * rather than placing the string directly in the generated class to
     * cut down on class size.
     * @return the error message
     */
    protected final String badConsArgMsg () {
        return "Invalid constructor argument for " + getClass().getName();
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
     * @param a
     * @param b
     * @return The value of the first argument.
     */
    public static final RTValue lastRef (RTValue a, RTValue b) {
        return a;
    }
    
    /** {@inheritDoc} */
    @Override
    public final RTValue internalUnwrapOpaque() {
        if (this instanceof CAL_Opaque) {                       
            Object opaqueValue = ((CAL_Opaque)this).getOpaqueValue();
            if (opaqueValue instanceof RTValue) {
                return (RTValue)opaqueValue;
            }            
        } 

        return this;          
    }
    
    @Override
    public final MachineType debug_getMachineType() {
        return MachineType.LECC;
    }

}
