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
 * ArraySupport.java
 * Creation date: Jun 21, 2006.
 * By: Joseph Wong
 */
package org.openquark.cal.internal.foreignsupport.module.Array;

import org.openquark.cal.runtime.CalValue;

/**
 * Contains support methods for the Array module.
 *
 * @author Joseph Wong
 */
public class ArraySupport {

    /** Integer representation of the CAL enumeration value Array.CharType */
    private static final int CHAR_TYPE = 0;
    /** Integer representation of the CAL enumeration value Array.BooleanType */
    private static final int BOOLEAN_TYPE = 1;
    /** Integer representation of the CAL enumeration value Array.ByteType */
    private static final int BYTE_TYPE = 2;
    /** Integer representation of the CAL enumeration value Array.ShortType */
    private static final int SHORT_TYPE = 3;
    /** Integer representation of the CAL enumeration value Array.IntType */
    private static final int INT_TYPE = 4;
    /** Integer representation of the CAL enumeration value Array.LongType */
    private static final int LONG_TYPE = 5;
    /** Integer representation of the CAL enumeration value Array.FloatType */
    private static final int FLOAT_TYPE = 6;
    /** Integer representation of the CAL enumeration value Array.DoubleType */
    private static final int DOUBLE_TYPE = 7;
    /** Integer representation of the CAL enumeration value Array.ObjectType */
    private static final int OBJECT_TYPE = 8;
    /** Integer representation of the CAL enumeration value Array.CalValueType */
    private static final int CAL_VALUE_TYPE = 9;

    /**
     * Private constructor.
     */
    private ArraySupport() {
    }

    /**
     * Returns the integer representation of the CAL enumeration value (of type Array.ElementType)
     * for the underlying value type of an internal value.
     * @param value the internal value.
     * @return the element type as an integer.
     */
    public static int getElementType(CalValue value) {
        
        CalValue.DataType type = value.getDataType();
        
        switch (type) {
        case CHAR:
            return CHAR_TYPE;
        case BOOLEAN:
            return BOOLEAN_TYPE;
        case BYTE:
            return BYTE_TYPE;
        case SHORT:
            return SHORT_TYPE;
        case INT:
            return INT_TYPE;
        case LONG:
            return LONG_TYPE;
        case FLOAT:
            return FLOAT_TYPE;
        case DOUBLE:
            return DOUBLE_TYPE;
        case OBJECT:
            return OBJECT_TYPE;
        case OTHER:
            return CAL_VALUE_TYPE;
        default:
            throw new IllegalArgumentException("Unknown CalValue type: " + type);
        }
    }
}
