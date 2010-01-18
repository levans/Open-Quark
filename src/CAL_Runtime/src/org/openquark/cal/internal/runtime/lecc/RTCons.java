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
 * RTCons.java
 * Created: May 14, 2003 2:51:53 PM
 * By: RCypher  
 */

package org.openquark.cal.internal.runtime.lecc;

import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.runtime.CalValue;
import org.openquark.cal.runtime.ErrorInfo;


/**
 * This is the RTCons class/interface.
 * This serves as the base class for non-primitive types.
 * <p>
 * lecc automatically generates classes for data types that are subclasses of RTCons.
 * For example, for the Prelude.List type, a TYPE_List class deriving from RTCons will be generated.
 * TYPE_List will have 2 derived classes as static inner classes CAL_Nil and CAL_Cons.
 * CAL_Nil and CAL_Cons both have $instance static final fields for the use of Prelude.Nil and Prelude.Cons
 * as functions. In this sense they are truly RTSupercombinator derived classes. However, they also are able
 * to constructor values e.g. Cons applied to 2 RTValue arguments. In this sense they do not obey the RTSupercombinator
 * contract, and so some special treatment is needed.
 * 
 * Created: May 14, 2003 2:51:53 PM
 * @author RCypher
 */
public abstract class RTCons extends RTSupercombinator {
     
    /** 
     * Set a string which shows the sequence of call calls that
     * led to the creation of this object.
     * This class will be overridden by generated derived classes.
     */
    public void setCreationCallChain() {
    }

    /**
     * Check that this instance represents the expected data constructor.
     * If it doesn't throws and error.
     * @param dcOrdinal - ordinal of the expected data constructor
     * @param errorInfo
     * @throws CALExecutorException
     */
    protected final void checkDCOrdinalForFieldSelection (int dcOrdinal, ErrorInfo errorInfo) throws CALExecutorException {
        if (dcOrdinal != getOrdinalValue()) {
            String message = "Wrong data constructor in data constructor field selection or local pattern match declaration.  Expecting: " + getModuleName() + "." + getDCNameByOrdinal(dcOrdinal) + 
            ", found: " + getQualifiedName() + ".";
            throw new CALExecutorException.ExternalException.PatternMatchFailure(errorInfo, message, null);
        }
    }
    
    public RTValue getFieldByIndex (int dcOrdinal, int fieldIndex, ErrorInfo errorInfo) throws CALExecutorException {
        checkDCOrdinalForFieldSelection(dcOrdinal, errorInfo);
        badFieldIndexInGetFieldByIndex(fieldIndex);
        return badValue(errorInfo, "Attempt to call getFieldByIndex() on " + getClass().getName() + " with field index " + fieldIndex + " on " + getClass().getName());
    }

    public Object getFieldByIndex_As_Object (int dcOrdinal, int fieldIndex, ErrorInfo errorInfo) throws CALExecutorException {
        checkDCOrdinalForFieldSelection(dcOrdinal, errorInfo);
        badFieldIndexInGetFieldByIndex(fieldIndex);
        return badValue(errorInfo, "Attempt to call getFieldByIndex_As_Object() on " + getClass().getName() + " with field index " + fieldIndex + " on " + getClass().getName());
    }

    public boolean getFieldByIndex_As_Boolean (int dcOrdinal, int fieldIndex, ErrorInfo errorInfo) throws CALExecutorException {
        checkDCOrdinalForFieldSelection(dcOrdinal, errorInfo);
        badFieldIndexInGetFieldByIndex(fieldIndex);
        badValue(errorInfo, "Attempt to call getFieldByIndex_As_Boolean() on " + getClass().getName() + " with field index " + fieldIndex + " on " + getClass().getName());
        return false;
    }
    
    public byte getFieldByIndex_As_Byte (int dcOrdinal, int fieldIndex, ErrorInfo errorInfo) throws CALExecutorException {
        checkDCOrdinalForFieldSelection(dcOrdinal, errorInfo);
        badFieldIndexInGetFieldByIndex(fieldIndex);
        badValue(errorInfo, "Attempt to call getFieldByIndex_As_Byte() on " + getClass().getName() + " with field index " + fieldIndex + " on " + getClass().getName());
        return (byte)0;
    }
    
    public short getFieldByIndex_As_Short (int dcOrdinal, int fieldIndex, ErrorInfo errorInfo) throws CALExecutorException {
        checkDCOrdinalForFieldSelection(dcOrdinal, errorInfo);
        badFieldIndexInGetFieldByIndex(fieldIndex);
        badValue(errorInfo, "Attempt to call getFieldByIndex_As_Short() on " + getClass().getName() + " with field index " + fieldIndex + " on " + getClass().getName());
        return 0;
    }
    
    public char getFieldByIndex_As_Character (int dcOrdinal, int fieldIndex, ErrorInfo errorInfo) throws CALExecutorException {
        checkDCOrdinalForFieldSelection(dcOrdinal, errorInfo);
        badFieldIndexInGetFieldByIndex(fieldIndex);
        badValue(errorInfo, "Attempt to call getFieldByIndex_As_Character() on " + getClass().getName() + " with field index " + fieldIndex + " on " + getClass().getName());
        return ' ';
    }
    
    public int getFieldByIndex_As_Int (int dcOrdinal, int fieldIndex, ErrorInfo errorInfo) throws CALExecutorException {
        checkDCOrdinalForFieldSelection(dcOrdinal, errorInfo);
        badFieldIndexInGetFieldByIndex(fieldIndex);
        badValue(errorInfo, "Attempt to call getFieldByIndex_As_Int() on " + getClass().getName() + " with field index " + fieldIndex + " on " + getClass().getName());
        return 0;
    }
    
    public long getFieldByIndex_As_Long (int dcOrdinal, int fieldIndex, ErrorInfo errorInfo) throws CALExecutorException {
        checkDCOrdinalForFieldSelection(dcOrdinal, errorInfo);
        badFieldIndexInGetFieldByIndex(fieldIndex);
        badValue(errorInfo, "Attempt to call getFieldByIndex_As_Long() on " + getClass().getName() + " with field index " + fieldIndex + " on " + getClass().getName());
        return 0;
    }
    
    public double getFieldByIndex_As_Double (int dcOrdinal, int fieldIndex, ErrorInfo errorInfo) throws CALExecutorException {
        checkDCOrdinalForFieldSelection(dcOrdinal, errorInfo);
        badFieldIndexInGetFieldByIndex(fieldIndex);
        badValue(errorInfo, "Attempt to call getFieldByIndex_As_Double() on " + getClass().getName() + " with field index " + fieldIndex + " on " + getClass().getName());
        return 0;
    }
    
    public float getFieldByIndex_As_Float (int dcOrdinal, int fieldIndex, ErrorInfo errorInfo) throws CALExecutorException {
        checkDCOrdinalForFieldSelection(dcOrdinal, errorInfo);
        badFieldIndexInGetFieldByIndex(fieldIndex);
        badValue(errorInfo, "Attempt to call getFieldByIndex_As_Float() on " + getClass().getName() + " with field index " + fieldIndex + " on " + getClass().getName());
        return 0;
    }
    
    public String getFieldByIndex_As_java_lang_String (int dcOrdinal, int fieldIndex, ErrorInfo errorInfo) throws CALExecutorException {
        checkDCOrdinalForFieldSelection(dcOrdinal, errorInfo);
        badFieldIndexInGetFieldByIndex(fieldIndex);
        badValue(errorInfo, "Attempt to call getFieldByIndex_As_java_lang_String() on " + getClass().getName() + " with field index " + fieldIndex + " on " + getClass().getName());
        return "";
    }

    protected String getDCNameByOrdinal (int dcOrdinal) {
        badValue(null, "Attempt to call getDCNameByOrdinal() on " + getClass().getName() + ".");
        return "";
    }
    
    /**
     * See the class comment as to why RTCons instances do not obey the contract of RTSupercombinator. If this function returns
     * true, then this particular instance of the given data constructor class type is the one that *does* actually obey the
     * RTSupercombinator contract.
     * 
     * In particular, it returns true for all 0 arity data constructors, as well as the functional singleton for positive
     * arity data constructors (corresponding to the $instance field in the generated code for the data constructor).
     * 
     * @return true if this is the special singleton instance of the given data constructor representing the use of the
     *    data constructor as a function. For example, there is a Cons singleton instance, which is used for partially applied
     *    applications of Cons. When the function is actually evaluated, and we have a Cons application to 2 arguments, this
     *    creates a Cons node which is not a singleton, but more like a subclass of RTData.
     */
    public boolean isFunctionSingleton() {
        //in general, 0-arity data constructors are always represented by a singleton. For positive arity functions,
        //this must be overridden in the generated data constructor class.
        return true;
    }
      
    /**     
     * {@inheritDoc}
     */
    @Override
    public final int debug_getNChildren() {    
        if (isFunctionSingleton()) {
            return 0;
        }
        return getArity();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public CalValue debug_getChild(int childN) {
        if (isFunctionSingleton()) {        
            throw new IndexOutOfBoundsException();  
        }
        
        throw new IllegalStateException("debug_getChild must be overridden for non function singletons.");
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public final String debug_getNodeStartText() {
        if (isFunctionSingleton()) {
            return getQualifiedName();
        } 
        
        return "(" + getQualifiedName();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public final String debug_getNodeEndText() {
        if (isFunctionSingleton()) {
            return "";
        }
        return ")";
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public final String debug_getChildPrefixText(int childN) {
        if (isFunctionSingleton()) {            
            throw new IndexOutOfBoundsException();
        }
        
        if (childN >= 0 && childN < getArity()) {
            return " ";
        }
        
        throw new IndexOutOfBoundsException();        
    }    
    
    /**
     * Method called when a field accessor (i.e. get_fieldName()) is 
     * called for a field that doesn't exist in this instance of RTCons.
     * @param fieldName
     * @return Won't really return anything since it will always throw an exception.
     */
    protected final RTValue badFieldAccessor (String fieldName) {
        return RTValue.badValue("Unable to retrieve field " + fieldName + " from " + getQualifiedName() + ".");
    }

    protected final boolean  badFieldAccessor_Boolean (String fieldName) {
        return RTValue.badValue_boolean(null, "Unable to retrieve field " + fieldName + " from " + getQualifiedName() + ".");
    }
    protected final byte badFieldAccessor_Byte (String fieldName) {
        return RTValue.badValue_byte(null, "Unable to retrieve field " + fieldName + " from " + getQualifiedName() + ".");
    }
    protected final char badFieldAccessor_Character (String fieldName) {
        return RTValue.badValue_char(null, "Unable to retrieve field " + fieldName + " from " + getQualifiedName() + ".");
    }
    protected final double badFieldAccessor_Double (String fieldName) {
        return RTValue.badValue_double(null, "Unable to retrieve field " + fieldName + " from " + getQualifiedName() + ".");
    }
    protected final float badFieldAccessor_Float (String fieldName) {
        return RTValue.badValue_float(null, "Unable to retrieve field " + fieldName + " from " + getQualifiedName() + ".");
    }
    protected final int badFieldAccessor_Int (String fieldName) {
        return RTValue.badValue_int(null, "Unable to retrieve field " + fieldName + " from " + getQualifiedName() + ".");
    }
    protected final long badFieldAccessor_Long (String fieldName) {
        return RTValue.badValue_long(null, "Unable to retrieve field " + fieldName + " from " + getQualifiedName() + ".");
    }
    protected final Object badFieldAccessor_Object (String fieldName) {
        return RTValue.badValue_Object(null, "Unable to retrieve field " + fieldName + " from " + getQualifiedName() + ".");
    }
    protected final short badFieldAccessor_Short (String fieldName) {
        return RTValue.badValue_short(null, "Unable to retrieve field " + fieldName + " from " + getQualifiedName() + ".");
    }

    /**
     * Helper function called by generated code if an invalid field index is encountered in 
     * a generated instance of getFieldByIndex.
     * @param index
     */
    protected void badFieldIndexInGetFieldByIndex (int index) {
        RTValue.badValue("Unable to retrieve field for index " + index + " in data constructor " + getQualifiedName() + ".");
    }
}

