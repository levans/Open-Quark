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
 * JoinType.java
 * Creation date: Apr 16, 2007.
 * By: Richard Webster
 */
package org.openquark.cal.foreignsupport.module.Sql;

/**
 * An enumeration of the possible join type values.
 * 
 * @author Richard Webster
 */
public final class JoinType {
    /** Constants for various join type values */
    public static final int INNER_JOIN_VALUE       = 0;
    public static final int LEFT_OUTER_JOIN_VALUE  = 1;
    public static final int RIGHT_OUTER_JOIN_VALUE = 2;
    public static final int FULL_OUTER_JOIN_VALUE  = 3;

    /** JoinType constants */
    public static final JoinType INNER_JOIN       = new JoinType(INNER_JOIN_VALUE);
    public static final JoinType LEFT_OUTER_JOIN  = new JoinType(LEFT_OUTER_JOIN_VALUE);
    public static final JoinType RIGHT_OUTER_JOIN = new JoinType(RIGHT_OUTER_JOIN_VALUE);
    public static final JoinType FULL_OUTER_JOIN  = new JoinType(FULL_OUTER_JOIN_VALUE);

    /** The integer value that distinguishes among types */
    private final int value;

    /**
     * This class should never be instantiated.
     */
    private JoinType(int value) {
        this.value = value;
    }
    
    /**
     * Returns the integer value that uniquely identifies the join type.
     * @return int
     */
    public int getValue() {
        return value;
    }
    
    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != JoinType.class) {
            return false;
        }
        return getValue() == ((JoinType) obj).getValue();
    }
    
    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return new Integer(getValue()).hashCode();
    }
    
    /** {@inheritDoc} */
    @Override
    public String toString() {
        switch (value) {
        case INNER_JOIN_VALUE :        return "InnerJoin";
        case LEFT_OUTER_JOIN_VALUE :   return "LeftOuterJoin";
        case RIGHT_OUTER_JOIN_VALUE :  return "RightOuterJoin";
        case FULL_OUTER_JOIN_VALUE :   return "FullOuterJoin";
        default :                      return "Unknown";
        }
    }
    
    /**
     * Converts an integer to an <code>JoinType</code> value.
     * @param value
     * @return JoinType
     */
    public static JoinType fromInt(int value) {
        switch (value) {
        case INNER_JOIN_VALUE :        return INNER_JOIN;
        case LEFT_OUTER_JOIN_VALUE :   return LEFT_OUTER_JOIN;
        case RIGHT_OUTER_JOIN_VALUE :  return RIGHT_OUTER_JOIN;
        case FULL_OUTER_JOIN_VALUE :   return FULL_OUTER_JOIN;
        default:
            throw new IllegalArgumentException("Invalid join type value: " + value);
        }
    }
}
