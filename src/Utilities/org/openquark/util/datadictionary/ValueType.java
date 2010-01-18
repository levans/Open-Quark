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
 * ValueType.java
 * Creation date (Apr 20, 2004).
 * By: Richard Webster
 */
package org.openquark.util.datadictionary;

import org.openquark.util.database.SqlType;

/**
 * A type-safe enum corresponding to the ValueType.ValueType CAL 'enum'.
 */
public final class ValueType {
    // Note: these integer values must match the values in ValueType.fromEnumForValueType.
    public static final int _nullType = 0;
    public static final int _stringType = 1;
    public static final int _intType = 2;
    public static final int _doubleType = 3;
    public static final int _booleanType = 4;
    public static final int _timeType = 5;
    public static final int _binaryType = 6;

    private static final String invalid_index_msg = "Invalid enum index ";
    private static final String _unknownStr = "unknown";
    private static final String _nullStr = "null";
    private static final String _stringStr = "string";
    private static final String _intStr = "int";
    private static final String _doubleStr = "double";
    private static final String _booleanStr = "boolean";
    private static final String _timeStr = "time";
    private static final String _binaryStr = "binary";

    public static final ValueType nullType = new ValueType(_nullType);
    public static final ValueType stringType = new ValueType(_stringType);
    public static final ValueType intType = new ValueType(_intType);
    public static final ValueType doubleType = new ValueType(_doubleType);
    public static final ValueType booleanType = new ValueType(_booleanType);
    public static final ValueType timeType = new ValueType(_timeType);
    public static final ValueType binaryType = new ValueType(_binaryType);

    /** The integer value of the ValueType. */
    final private int _value;

    /**
     * ValueType constructor.
     * @param enumVal
     */
    private ValueType(int enumVal) {
        _value = enumVal;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    final public String toString() {
        switch (_value) {
            case _nullType :
                return _nullStr;
            case _stringType :
                return _stringStr;
            case _intType :
                return _intStr;
            case _doubleType :
                return _doubleStr;
            case _booleanType :
                return _booleanStr;
            case _timeType :
                return _timeStr;
            case _binaryType :
                return _binaryStr;
        }
        return _unknownStr;
    }

    /**
     * Returns the integer value of the ValueType.
     * @return the integer value of the ValueType
     */
    public int value() {
        return _value;
    }

    /**
     * Return the ValueType corresponding to the specified integer value.
     * @param val  an integer value of a ValueType
     * @return the ValueType corresponding to the integer value
     */
    final public static ValueType fromInt(int val) {
        switch (val) {
            case _nullType :
                return nullType;
            case _stringType :
                return stringType;
            case _intType :
                return intType;
            case _doubleType :
                return doubleType;
            case _booleanType :
                return booleanType;
            case _timeType :
                return timeType;
            case _binaryType :
                return binaryType;
        }
        throw new IndexOutOfBoundsException(invalid_index_msg + val);
    }

    /**
     * Method fromString
     * 
     * @param val
     * 
     * @return Returns the ValueType corresponding to the given string (cf. toString)
     */
    final public static ValueType fromString (String val) {
        if (val.equals (_nullStr)) {
            return nullType;
        } else if (val.equals (_stringStr)) {
            return stringType;
        } else if (val.equals (_intStr)) {
            return intType;
        } else if (val.equals (_doubleStr)) {
            return doubleType;
        } else if (val.equals (_booleanStr)) {
            return booleanType;
        } else if (val.equals (_timeStr)) {
            return timeType;
        } else if (val.equals (_binaryStr)) {
            return binaryType;
        }
        
        throw new IllegalArgumentException ("Invalid ValueType string: " + val);
    }

    /**
     * Returns the best ValueType for the specified SQL type.
     * NullType will be returned if there are no other appropriate ValueType mappings.
     */
    final public static ValueType fromSqlType(SqlType sqlType) {
        if (sqlType instanceof SqlType.SqlType_TinyInt
                || sqlType instanceof SqlType.SqlType_SmallInt
                || sqlType instanceof SqlType.SqlType_Integer) {
            return intType;
        }
        else if (sqlType instanceof SqlType.SqlType_BigInt     // BigInt values can be too large for a regular Int, so map them to the Double value type.
                || sqlType instanceof SqlType.SqlType_Decimal
                || sqlType instanceof SqlType.SqlType_Numeric
                || sqlType instanceof SqlType.SqlType_Real
                || sqlType instanceof SqlType.SqlType_Float
                || sqlType instanceof SqlType.SqlType_Double) {
            return doubleType;
        }
        else if (sqlType instanceof SqlType.SqlType_Bit
                || sqlType instanceof SqlType.SqlType_Boolean) {
            return booleanType;
        }
        else if (sqlType instanceof SqlType.SqlType_Char
                || sqlType instanceof SqlType.SqlType_VarChar
                || sqlType instanceof SqlType.SqlType_LongVarChar
                || sqlType instanceof SqlType.SqlType_Clob) {
            return stringType;
        }
        else if (sqlType instanceof SqlType.SqlType_Binary
                || sqlType instanceof SqlType.SqlType_VarBinary
                || sqlType instanceof SqlType.SqlType_LongVarBinary
                || sqlType instanceof SqlType.SqlType_Blob) {
            return binaryType;
        }
        else if (sqlType instanceof SqlType.SqlType_Date
                || sqlType instanceof SqlType.SqlType_Time
                || sqlType instanceof SqlType.SqlType_TimeStamp) {
            return timeType;
        }
        else {
            return nullType;
        }
    }
}
