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
 * FieldType.java
 * Created: Oct 22, 2004
 * By: Kevin Sit
 */
package org.openquark.util.datadictionary;

/**
 * A type-safe enum for dictionary field types.
 */
public final class FieldType {
    
    /** Constants for various field type values */
    public static final int DETAIL_VALUE = 0;
    public static final int DIMENSION_VALUE = 1;
    public static final int MEASURE_VALUE = 2;
    public static final int FILTER_VALUE = 3;
    
    /** FieldType constants */
    public static final FieldType DETAIL = new FieldType(DETAIL_VALUE);
    public static final FieldType DIMENSION = new FieldType(DIMENSION_VALUE);
    public static final FieldType MEASURE = new FieldType(MEASURE_VALUE);
    public static final FieldType FILTER = new FieldType(FILTER_VALUE);
    
    /** The integer value that distinguishes among types */
    private final int value;

    /**
     * This class should never be instantiated.
     */
    private FieldType(int value) {
        this.value = value;
    }
    
    /**
     * Returns the integer value that uniquely identifies a field type.
     * @return int
     */
    public int getValue() {
        return value;
    }
    
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != FieldType.class) {
            return false;
        }
        return getValue() == ((FieldType) obj).getValue();
    }
    
    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return new Integer(getValue()).hashCode();
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        switch (value) {
        case DETAIL_VALUE:
            return "Detail";
        case DIMENSION_VALUE:
            return "Dimension";
        case MEASURE_VALUE:
            return "Measure";
        case FILTER_VALUE:
            return "Filter";
        default:
            return "Unknown";
        }
    }
    
    /**
     * Converts an integer to a <code>FieldType</code> value.
     * @param value
     * @return FieldType
     */
    public static FieldType fromInt(int value) {
        switch (value) {
        case DETAIL_VALUE:
            return DETAIL;
        case DIMENSION_VALUE:
            return DIMENSION;
        case MEASURE_VALUE:
            return MEASURE;
        case FILTER_VALUE:
            return FILTER;
        default:
            throw new IllegalArgumentException("Invalid field type value: " + value);
        }
    }

}
