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
 * Time.java
 * Created: 15-Oct-2003
 * By: Rick Cameron
 */

package org.openquark.util.time;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ibm.icu.util.Calendar;

/**
 * Class Time
 * 
 * This class is an Abstract Data Type that represents a point in real-world time.
 * As an ADT it is immutable. This also has benefits for its use in CAL.
 * 
 * An instance represents a point in time that is a certain number of ticks before or after the base time.
 * The base time is 00:00:00 UTC 1 Jan 1970. A tick is 100 ns. Using a 64-bit counter, this gives a range of +/-29,000 years.
 * 
 * The type is meant to be portable to other languages & environments. In particular, it can be easily ported to C#/.NET.
 */
public final class Time implements Comparable<Time>, Serializable {
    
    static final long serialVersionUID = -5414456666706739082L;
    
    /** the base time of java.util.Date, relative to the base time of this class */
    private static final long javaUtilDateBase = 0;    

    private static final int ticksPerMillisecond = 10000; 
    /**/    static final int ticksPerSecond      = 10000000; 
    /**/    static final long ticksPerDay        = ticksPerSecond * 86400L;
    private static final int nanosecondsPerTick  = 100;

    /** base time is 00:00:00 UTC 1 Jan 1970, unit is 100 ns */
    private final long ticks; 
    
    /**
     * Method now
     * 
     * @return Returns a Time representing the current time
     */
    public static Time now () {
        return fromDate (new Date ());
    }
    
    /**
     * Method fromDate
     * 
     * @param date
     * @return Returns a Time the corresponds to the given Date
     */
    public static Time fromDate (Date date) {
        return new Time (javaUtilDateMillisecondsToTicks(date.getTime()));
    }

    /**
     * Method fromTimeStamp
     * 
     * @param timeStamp
     * @return Returns a Time the corresponds to the given Timestamp
     */
    public static Time fromTimeStamp (Timestamp timeStamp) {
        // Get the number of ticks from the timestamp and truncate this result to the nearest second.
        // The Timestamp.getTime() method will include part of the nanosecond fraction in the result.
        // This needs to be removed so that the full nanosecond fraction can be converted to ticks.) 
        long ticks = javaUtilDateMillisecondsToTicks(timeStamp.getTime());

        // Truncate the tick value to the nearest second.
        // Watch out for negative tick values.
        if (ticks < 0) {
            ticks -= ticksPerSecond - 1;
        }
        ticks /= ticksPerSecond;
        ticks *= ticksPerSecond;

        // Include the contribution of the nanosecond fraction in ticks.
        long nanoFraction = timeStamp.getNanos();
        ticks += (nanoFraction + nanosecondsPerTick / 2) / nanosecondsPerTick;

        return new Time(ticks);
    }

    /**
     * Method javaUtilDateMillisecondsToTicks
     * 
     * @param millis
     * @return Returns the tick count that corresponds to the given millisecond count, adjusting base times
     */
    private static long javaUtilDateMillisecondsToTicks (long millis) {
        return millis * ticksPerMillisecond + javaUtilDateBase;
    }
    
    /**
     * Method ticksToJavaUtilDateMilliseconds
     * 
     * @param ticks
     * @return Returns the millisecond count that corresponds to the given tick count, adjusting base times
     */
    private static long ticksToJavaUtilDateMilliseconds (long ticks) {
        return ((ticks - javaUtilDateBase) + ticksPerMillisecond / 2) / ticksPerMillisecond;
    }

    /**
     * Constructor Time
     * 
     * Constructs a Time from a year, month, day, hour, minute, second and ticks in the Gregorian calendar
     * using the specifed time zone.
     * 
     * @param year    - Gregorian year
     * @param month   - Gregorian month (1-origin)
     * @param day     - Gregorian day (1-origin)
     * @param hour    - hour
     * @param minute  - minute
     * @param second  - second
     * @param ticks   - ticks (100 ns units)
     * @param tz      - time zone
     */
    public Time (int year, int month, int day, int hour, int minute, int second, int ticks, TimeZone tz) {
        Calendar cal = Calendar.getInstance(tz.toICUTimeZone());

        cal.set(year, month - 1, day, hour, minute, second);
        cal.set(Calendar.MILLISECOND, 0);  
        long milliseconds = cal.getTimeInMillis();

        this.ticks = javaUtilDateMillisecondsToTicks(milliseconds) + ticks;
    }

    /**
     * Constructor Time
     * 
     * Constructs a Time from a year, month, day, hour, minute, second and ticks in the Gregorian calendar & UTC
     * 
     * @param year    - Gregorian year
     * @param month   - Gregorian month (1-origin)
     * @param day     - Gregorian day (1-origin)
     * @param hour    - UTC hour
     * @param minute  - UTC minute
     * @param second  - UTC second
     * @param ticks   - UTC ticks (100 ns units)
     */
    @SuppressWarnings(value="deprecation") //this is the only efficient way to do the conversion
    public Time (int year, int month, int day, int hour, int minute, int second, int ticks) {
        long milliseconds = Date.UTC(year - 1900, month - 1, day, hour, minute, second);
        
        this.ticks = javaUtilDateMillisecondsToTicks(milliseconds) + ticks;
    }

    /**
     * Constructor Time
     * 
     * Constructs a Time from a year, month, day, hour, minute and second in the Gregorian calendar & UTC
     * 
     * @param year    - Gregorian year
     * @param month   - Gregorian month (1-origin)
     * @param day     - Gregorian day (1-origin)
     * @param hour    - UTC hour
     * @param minute  - UTC minute
     * @param second  - UTC second
     */
    public Time (int year, int month, int day, int hour, int minute, int second) {
        this (year, month, day, hour, minute, second, 0);
    }
    
    /**
     * Constructor Time
     * 
     * Constructs a Time from a tick count (the number of 100 ns units from the epoch base time)
     * 
     * @param ticks
     */
    public Time (long ticks) {
        this.ticks = ticks;
    }
    
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Time && ticks == ((Time)obj).ticks;
    }
      
    /**
     * Method compareTo
     * 
     * @param other
     * @return Returns 1 if this Time is after the given one, -1 if it is before, and 0 if they are equal
     * {@inheritDoc}
     */
    public int compareTo(Time other) {
        long otherTicks = other.ticks;
        return (ticks == otherTicks) ? 0 : (ticks < otherTicks ? -1 : 1);
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
     * Method toDate
     * 
     * @return Returns the nearest Date to the time specified by this object
     */
    public Date toDate () {
        return new Date (ticksToJavaUtilDateMilliseconds(ticks));
    }

    /**
     * Get the day of the year, removes the time of day and returns just the (UTC) date.
     * @see java.sql.Date
     * @return java.sql.Date
     */
    public java.sql.Date trimToSQLDate() {
        long dayTicks = ticks / ticksPerDay;
        dayTicks *= ticksPerDay;
        return new java.sql.Date (ticksToJavaUtilDateMilliseconds(dayTicks));
    }

    /**
     * Get the time of day, removes the Day / Month / Year and returns just the time of day.
     * @see java.sql.Time
     * @return java.sql.Time
     */
    public java.sql.Time trimToSQLTime() {
        long timeTick = ticks % ticksPerDay;
        return new java.sql.Time (ticksToJavaUtilDateMilliseconds(timeTick));
    }    
        
    /**
     * Returns the year, month, day, hour, minute, seconds, and ticks from the time value
     * using the specified timezone.
     * @param tz  the timezone for which the time parts should be extracted
     * @return Returns the year, month, day, hour, minute, seconds, and ticks from the time value
     */
    public int [] timeParts (TimeZone tz) {
        int [] result = new int [7]; // year, month, day, hour, minute, second, ticks
        
        long truncatedTicks = (ticks / ticksPerSecond) * ticksPerSecond;
        
        Calendar utcCalendar = Calendar.getInstance(tz.toICUTimeZone());
        
        utcCalendar.setTimeInMillis(ticksToJavaUtilDateMilliseconds(truncatedTicks));
        
        result [0] = utcCalendar.get(Calendar.YEAR);
        result [1] = utcCalendar.get(Calendar.MONTH) + 1;
        result [2] = utcCalendar.get(Calendar.DATE);
        result [3] = utcCalendar.get(Calendar.HOUR_OF_DAY);
        result [4] = utcCalendar.get(Calendar.MINUTE);
        result [5] = utcCalendar.get(Calendar.SECOND);
        result [6] = (int) (ticks % ticksPerSecond);
        
        return result;
    }

    /**
     * Returns the year, month, day, hour, minute, seconds, and ticks from the time value
     * using the specified timezone as a list of Integers.
     * @param tz  the timezone for which the time parts should be extracted
     * @return Returns the year, month, day, hour, minute, seconds, and ticks from the time value as a list of Integers
     */
    public List<Integer> timePartsList (TimeZone tz) {
        int [] parts = timeParts (tz);

        List<Integer> partsList = new ArrayList<Integer>();
        for (int i = 0, n = parts.length; i < n; ++i) {
            partsList.add(new Integer(parts[i]));
        }
        return partsList;
    }
    
    /**
     * Method getDayOfWeek
     * 
     * @param tz
     * 
     * @return Returns an int representing the day of the week in the given time zone, where 0 = Monday
     */
    public int getDayOfWeek (TimeZone tz ) {
        long truncatedTicks = (ticks / ticksPerSecond) * ticksPerSecond;
        
        Calendar utcCalendar = Calendar.getInstance(tz.toICUTimeZone());
        
        utcCalendar.setTimeInMillis(ticksToJavaUtilDateMilliseconds(truncatedTicks));

        int icuDayOfWeek = utcCalendar.get(Calendar.DAY_OF_WEEK);
        
        return ((icuDayOfWeek - Calendar.MONDAY) + 7) % 7;
    }

    /**
     * Method toUTC
     * 
     * @return Returns the year, month, day, hour, minute, seconds, and ticks from the time value for UTC
     */
    public int [] toUTC () {
        return timeParts(TimeZone.utc());
    }

    /**
     * Method getTicks
     * 
     * @return Returns the number of ticks since the epoch base time
     */
    public long getTicks () {
        return ticks;
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        // TODO rbc: add in the ticks
        return toDate ().toString();
    }

    /**
     * Method subtract
     * 
     * @param t
     * @return Returns the Duration that represents the difference between this Time and the given Time
     */
    public Duration subtract (Time t) {
        return new Duration (ticks - t.ticks);
    }
    
    /**
     * Method add
     * 
     * @param d
     * @return Returns the Duration that represents the sum of this Time and the given Time
     */
    public Time add (Duration d) {
        return new Time (ticks + d.getTicks());
    }

    /**
     * Returns a string representing the serialized form of the Time value.
     */
    public String toSerializedForm() {
        // TODO rbc: use a nicer format
        return Long.toString(getTicks());
    }

    /**
     * Returns a Time value based on the serialized value provided.
     */
    public static Time fromSerializedForm(String serializedTime) {
        // TODO: what should happen if the serialized value isn't in the correct format?
        return new Time (Long.parseLong(serializedTime));
    }
}
