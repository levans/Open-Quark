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
 * Duration.java
 * Created: 4-Nov-2003
 * By: Rick Cameron
 */

package org.openquark.util.time;

import java.io.Serializable;

/**
 * A Duration represents an amount of time. It is measured using the same units as the Time class: 100 ns 'ticks'
 * The amount of time may be positive or negative
 * 
 * This class is an Abstract Data Type and is therefore immutable.
 * 
 * @author Rick Cameron
 */
public final class Duration implements Comparable<Duration>, Serializable {
    
    static final long serialVersionUID = 7932678672323527139L;
    
    /** One tick is 100 ns */
    private final long ticks;
    
    /**
     * Method fromTicks
     * 
     * @param ticks
     * @return Returns a Duration that corresponds to the given tick count
     */
    public static Duration fromTicks (long ticks) {
        return new Duration (ticks);
    }

    /**
     * Method fromSeconds
     * 
     * @param seconds An integer number of seconds
     * @return Returns a Duration that corresponds to the given count of seconds
     */
    public static Duration fromSeconds (long seconds) {
        return new Duration (seconds * Time.ticksPerSecond);
    }

    /**
     * Method fromSeconds
     * 
     * @param seconds A number of seconds (which can have a fractional part)
     * @return Returns a Duration that corresponds to the given count of seconds
     */
    public static Duration fromSeconds (double seconds) {
        return new Duration (Math.round(seconds * Time.ticksPerSecond));
    }

    /**
     * Method fromDays
     * 
     * @param days An integer number of days
     * @return Returns a Duration that corresponds to the given count of days
     */
    public static Duration fromDays (long days) {
        return new Duration (days * Time.ticksPerDay);
    }

    /**
     * Method fromDays
     * 
     * @param days A number of days (which can have a fractional part)
     * @return Returns a Duration that corresponds to the given count of days
     */
    public static Duration fromDays (double days) {
        return new Duration (Math.round(days * Time.ticksPerDay));
    }

    /**
     * Constructor Duration
     * 
     * @param ticks
     */
    public Duration (long ticks) {
        this.ticks = ticks;
    }
    
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Duration && ticks == ((Duration)obj).ticks;
    }
    
    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        //this is equivalent to new Long (ticks).hashCode()
        //the hash function for Long is defined in the api for Long.hashCode.
        return (int)(ticks ^ (ticks >>> 32));
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Duration (" + ticks + " ticks)";
    }
   
    /**
     * Method compareTo
     * 
     * @param other
     * @return Returns 1 if this Duration is greater than the given one, -1 if it is less, and 0 if they are equal
     * {@inheritDoc}
     */
    public int compareTo(Duration other) {
        long otherTicks = other.ticks;
        return (ticks == otherTicks) ? 0 : (ticks < otherTicks ? -1 : 1);
    }

    /**
     * Method getTicks
     * 
     * @return Returns the number of ticks this Duration represents
     */
    public long getTicks () {
        return ticks;
    }

    /**
     * Returns the number of seconds in the duration.
     * @return the number of seconds in the duration
     */
    public double getSeconds() {
        return ((double) ticks) / Time.ticksPerSecond;
    }

    /**
     * Method subtract
     * 
     * @param d
     * @return Returns the Duration that represents the difference between this Duration and the given Duration
     */
    public Duration subtract (Duration d) {
        return new Duration (ticks - d.ticks);
    }
    
    /**
     * Method add
     * 
     * @param d
     * @return Returns the Duration that represents the sum of this Duration and the given Duration
     */
    public Duration add (Duration d) {
        return new Duration (ticks + d.ticks);
    }
}
