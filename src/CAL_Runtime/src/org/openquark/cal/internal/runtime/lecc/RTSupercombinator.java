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
 * RTSupercombinator.java
 * Created: Jan 11, 2002 at 2:53:21 PM 
 * By: LEvans  
 */
package org.openquark.cal.internal.runtime.lecc;

import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.runtime.CalValue;
import org.openquark.cal.runtime.ErrorInfo;


/**
 * RTSupercombinator represents an unapplied RTFunction.  
 * Direct subclasses implement the f() method which contains the implementation
 * of a function which reduces this RTFunction with all its applied arguments
 * when fully saturated.
 * <p>
 * Creation: Nov 11, 2002
 * @author LEvans
 */
public abstract class RTSupercombinator extends RTFunction {

    /**
     * Reduce this graph node.
     * @param ec
     * @return the reduced graph.
     * @throws CALExecutorException
     */
    @Override
    protected final RTValue reduce(RTExecutionContext ec) throws CALExecutorException {
        return this;
    }
    
    /**
     * Get the description of this function. This is the name with the arity
     * (fully saturated number of arguments) that it takes.
     * 
     * @return the description
     */
    String getDescription() {
        return getClassName() + "/" + getArity();
    }
          
    /**
     * Retrieve the module name for this CAL entity.    
     * @return the name of the module containing the corresponding CAL entity
     */
    abstract public String getModuleName ();

    /**
     * Retrieve the unqualified name for this CAL entity.
     * @return the unqualified name of the corresponding CAL entity.
     */
    abstract public String getUnqualifiedName (); 
    
    /**
     * While this function could simply be implemented in terms of the abstract functions
     * getModuleName() and getUnqualifiedName() (i.e. return new StringBuilder(getModuleName()).append('.').append(getUnqualifiedName()).toString();)
     * it is made abstract so that generated code can return a literal string value.
     * This is more optimal than building up the qualified name string each time.
     * @return the fully qualified name as a String e.g. "Cal.Core.Prelude.Left", "Cal.Core.Prelude.Cons".
     */
    abstract public String getQualifiedName();
    
    /**
     * Normally, this won't need to be overridden except for RTCons, which is not truly a supercombinator-
     * its subclasses model actual values of a data constructor applied to its arguments.     
     * {@inheritDoc}
     */
    @Override
    public int debug_getNChildren() {         
        return 0;
    }
    /**
     * Normally, this won't need to be overridden except for RTCons, which is not truly a supercombinator-
     * its subclasses model actual values of a data constructor applied to its arguments. 
     * {@inheritDoc}
     */
    @Override
    public CalValue debug_getChild(int childN) {     
        throw new IndexOutOfBoundsException();        
    }
    /**
     * Normally, this won't need to be overridden except for RTCons, which is not truly a supercombinator-
     * its subclasses model actual values of a data constructor applied to its arguments. 
     * {@inheritDoc}
     */
    @Override
    public String debug_getNodeStartText() {       
        return getQualifiedName();
    }
    /**
     * Normally, this won't need to be overridden except for RTCons, which is not truly a supercombinator-
     * its subclasses model actual values of a data constructor applied to its arguments. 
     * {@inheritDoc}
     */
    @Override
    public String debug_getNodeEndText() {       
        return "";
    }
    /**
     * Normally, this won't need to be overridden except for RTCons, which is not truly a supercombinator-
     * its subclasses model actual values of a data constructor applied to its arguments. 
     * {@inheritDoc}
     */
    @Override
    public String debug_getChildPrefixText(int childN) {       
        throw new IndexOutOfBoundsException();            
    } 
    
    /**
     * A helper function used to generate the start of the function tracing text.
     * This will display the thread name, if tracing thread name is enabled, followed by
     * displaying the name of the function being evaluated.
     * 
     * @param $ec
     * @return StringBuilder
     */
    protected final StringBuilder generateBeginningTraceText(RTExecutionContext $ec) {
         
        StringBuilder $sb;
        
        if ($ec.traceShowsThreadName()) {
            $sb = new StringBuilder(Thread.currentThread().getName()).append("> ");
        } else {
            $sb = new StringBuilder();           
        } 
        
        return $sb.append(getQualifiedName());
    }
    
    /**
     * Function called when a switch statement in the generated code
     * encounters an invalid index. (i.e. an index which doesn't correspond
     * to any data constructor etc.)
     * @param errorInfo
     * @return nothing since this will always throw an exception.
     */
    protected final RTValue badSwitchIndex (ErrorInfo errorInfo) {
        return RTValue.badValue (errorInfo, "Illegal fall through to default case in " + getQualifiedName() + ".");
    }
    
    /**
     * Indicate that we have a bad value by throwing an exception.
     * This is used in generated methods that return a boolean.
     * @param errorInfo
     * @return boolean NEVER RETURNED.  This method 'returns' an boolean in order to be usable in 
     * boolean expressions
     */
    protected final boolean badSwitchIndex_boolean (ErrorInfo errorInfo) {
        return RTValue.badValue_boolean (errorInfo, "Illegal fall through to default case in " + getQualifiedName() + ".");
    }

    /**
     * Indicate that we have a bad value by throwing an exception.
     * This is used in generated methods that return a byte.
     * @param errorInfo
     * @return byte NEVER RETURNED.  This method 'returns' an byte in order to be usable in 
     * byte expressions
     */
    protected final byte badSwitchIndex_byte (ErrorInfo errorInfo) {
        return RTValue.badValue_byte (errorInfo, "Illegal fall through to default case in " + getQualifiedName() + ".");
    }
    
    /**
     * Indicate that we have a bad value by throwing an exception.
     * This is used in generated methods that return a char.
     * @param errorInfo
     * @return char NEVER RETURNED.  This method 'returns' an char in order to be usable in 
     * char expressions
     */
    protected final char badSwitchIndex_char (ErrorInfo errorInfo) {
        return RTValue.badValue_char (errorInfo, "Illegal fall through to default case in " + getQualifiedName() + ".");
    }
    
    /**
     * Indicate that we have a bad value by throwing an exception.
     * This is used in generated methods that return a double.
     * @param errorInfo
     * @return double NEVER RETURNED.  This method 'returns' an double in order to be usable in 
     * double expressions
     */
    protected final double badSwitchIndex_double (ErrorInfo errorInfo) {
        return RTValue.badValue_double (errorInfo, "Illegal fall through to default case in " + getQualifiedName() + ".");
    }
    
    /**
     * Indicate that we have a bad value by throwing an exception.
     * This is used in generated methods that return a float.
     * @param errorInfo
     * @return float NEVER RETURNED.  This method 'returns' an float in order to be usable in 
     * float expressions
     */
    protected final float badSwitchIndex_float (ErrorInfo errorInfo) {
        return RTValue.badValue_float(errorInfo, "Illegal fall through to default case in " + getQualifiedName() + ".");
    }

    /**
     * Indicate that we have a bad value by throwing an exception.
     * This is used in generated methods that return a int.
     * @param errorInfo
     * @return int NEVER RETURNED.  This method 'returns' an int in order to be usable in 
     * int expressions
     */
    protected final int badSwitchIndex_int (ErrorInfo errorInfo) {
        return RTValue.badValue_int (errorInfo, "Illegal fall through to default case in " + getQualifiedName() + ".");
    }
    
    /**
     * Indicate that we have a bad value by throwing an exception.
     * This is used in generated methods that return a long.
     * @param errorInfo
     * @return long NEVER RETURNED.  This method 'returns' an long in order to be usable in 
     * long expressions
     */
    protected final long badSwitchIndex_long (ErrorInfo errorInfo) {
        return RTValue.badValue_long (errorInfo, "Illegal fall through to default case in " + getQualifiedName() + ".");
    }
    
    /**
     * Indicate that we have a bad value by throwing an exception.
     * This is used in generated methods that return a Object.
     * @param errorInfo
     * @return Object NEVER RETURNED.  This method 'returns' an Object in order to be usable in 
     * Object expressions
     */
    protected final Object badSwitchIndex_Object (ErrorInfo errorInfo) {
        return RTValue.badValue_Object (errorInfo, "Illegal fall through to default case in " + getQualifiedName() + ".");
    }
    
    /**
     * Indicate that we have a bad value by throwing an exception.
     * This is used in generated methods that return a short.
     * @param errorInfo
     * @return short NEVER RETURNED.  This method 'returns' an short in order to be usable in 
     * short expressions
     */
    protected final short badSwitchIndex_short (ErrorInfo errorInfo) {
        return RTValue.badValue_short (errorInfo, "Illegal fall through to default case in " + getQualifiedName() + ".");
    }
    

    /**
     * Function called when a switch statement in the generated code encounters
     * an unhandled switch index.  i.e. the index is valid (corresponds to a DC)
     * but no case was provided.
     * @param errorInfo
     * @param dcName
     * @return nothing because this will always throw an exception.  A return type is declared 
     *     to make this function easier to use in generated code.
     * @throws CALExecutorException
     */
    protected final RTValue unhandledSwitchIndex (ErrorInfo errorInfo, String dcName) throws CALExecutorException {
        String errorMessage = "Unhandled case for " + dcName + " in " + getQualifiedName() + ".";
        CALExecutorException e = new CALExecutorException.ExternalException.PatternMatchFailure(errorInfo, errorMessage, null);
        if (System.getProperty(LECCMachineConfiguration.DEBUG_INFO_PROP) != null) {
            e.printStackTrace();
        }
        throw e;        
    }
    
    /**
     * Special handling for case expressions on the Prelude.Char type.
     * 
     * For example, called in the case
     * (\x -> case x of 'a' -> "letter a";) 'b'
     * 
     * @param errorInfo
     * @return nothing because this will always throw an exception.  A return type is declared 
     *     to make this function easier to use in generated code.
     * @throws CALExecutorException
     */
    protected final RTValue unhandledSwitchIndexForCharPattern (ErrorInfo errorInfo) throws CALExecutorException {
        //todoBI it would be better to show the actual Char value that was not handled in the error message
        return unhandledSwitchIndex(errorInfo, "Char pattern");              
    } 
    
    /**
     * Special handling for case expressions on the Prelude.Int type.
     * 
     * For example, called in the case
     * (\x -> case x of 1 -> "one";) (2 :: Int)    
     * 
     * @param errorInfo
     * @return nothing because this will always throw an exception.  A return type is declared 
     *     to make this function easier to use in generated code.
     * @throws CALExecutorException
     */
    protected final RTValue unhandledSwitchIndexForIntPattern (ErrorInfo errorInfo) throws CALExecutorException {
        //todoBI it would be better to show the actual Int value that was not handled in the error message
        return unhandledSwitchIndex(errorInfo, "Int pattern");              
    }        
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final DataType getDataType() {
        return DataType.OTHER;
    }
}
