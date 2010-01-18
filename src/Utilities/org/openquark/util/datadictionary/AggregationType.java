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
 * AggregationType.java
 * Created: May 24, 2005
 * By: Richard Webster
 */
package org.openquark.util.datadictionary;

/**
 * A type-safe enum for aggregation types.
 */
public final class AggregationType {

    /** Constants for various aggregation type values */
    public static final int NONE_VALUE = 0;
    public static final int SUM_VALUE = 1;
    public static final int MAX_VALUE = 2;
    public static final int MIN_VALUE = 3;
    public static final int AVG_VALUE = 4;
    public static final int COUNT_VALUE = 5;
    public static final int DISTINCT_COUNT_VALUE = 6;

    /** AggregationType constants */
    public static final AggregationType NONE  = new AggregationType(NONE_VALUE);
    public static final AggregationType SUM   = new AggregationType(SUM_VALUE);
    public static final AggregationType MAX   = new AggregationType(MAX_VALUE);
    public static final AggregationType MIN   = new AggregationType(MIN_VALUE);
    public static final AggregationType AVG   = new AggregationType(AVG_VALUE);
    public static final AggregationType COUNT = new AggregationType(COUNT_VALUE);
    public static final AggregationType DISTINCT_COUNT = new AggregationType(DISTINCT_COUNT_VALUE);

    /** The integer value that distinguishes among types */
    private final int value;

    /**
     * This class should never be instantiated.
     */
    private AggregationType(int value) {
        this.value = value;
    }
    
    /**
     * Returns the integer value that uniquely identifies the aggregation type.
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
        if (obj == null || obj.getClass() != AggregationType.class) {
            return false;
        }
        return getValue() == ((AggregationType) obj).getValue();
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
        case NONE_VALUE:
            return "None";
        case SUM_VALUE:
            return "Sum";
        case MAX_VALUE:
            return "Max";
        case MIN_VALUE:
            return "Min";
        case AVG_VALUE:
            return "Avg";
        case COUNT_VALUE:
            return "Count";
        case DISTINCT_COUNT_VALUE:
            return "DistinctCount";
        default:
            return "Unknown";
        }
    }
    
    /**
     * Converts an integer to an <code>AggregationType</code> value.
     * @param value
     * @return FieldType
     */
    public static AggregationType fromInt(int value) {
        switch (value) {
        case NONE_VALUE:
            return NONE;
        case SUM_VALUE:
            return SUM;
        case MAX_VALUE:
            return MAX;
        case MIN_VALUE:
            return MIN;
        case AVG_VALUE:
            return AVG;
        case COUNT_VALUE:
            return COUNT;
        case DISTINCT_COUNT_VALUE:
            return DISTINCT_COUNT;
        default:
            throw new IllegalArgumentException("Invalid aggregation type value: " + value);
        }
    }
}
